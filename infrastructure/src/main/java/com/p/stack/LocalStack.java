package com.p.stack;


import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalStack extends Stack {
    private final Vpc vpc;
    private final Cluster ecsCluster;

    public LocalStack(
            final App app,
            final String id,
            StackProps props
    ) {
        super(app, id, props);

        this.vpc = createVpc();

        DatabaseInstance authDatabase = createDatabaseInstance("AuthDB", "auth-database");
        DatabaseInstance patientDatabase = createDatabaseInstance("PatientDB", "patient-database");

        CfnHealthCheck authDBHealthCheck = createDBHealthCheck("AuthDBHealthCheck", authDatabase);
        CfnHealthCheck patientDBHealthCheck = createDBHealthCheck("PatientDBHealthCheck", patientDatabase);
        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();


        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                List.of(8085),
                authDatabase,
                Map.of("JWT_SECRET", "97be7f834c55c5b3763378eed3bb4467fbe0f1e614bd94956f3b2fedb0013b3a70126c15ad370c4524213a4990a0da27b772fcd596944941456ba2f3bb6a88052951f2e79c16b2ba71e35e61309c4531976fe5cbe4572b1ae48572f1a26563ae10da06a9e485a4bafa4e2a9afb43a6d74a9c11cda740c67a3534f97484e055e05b98afb2d5ac149a1e333b7b685a1e55b79e44526ed961a694a74fab54da3158ba3c49858f8bc0b4ed1cae632a973c52f1f649f89b59b41a0d0fb01bed7b0e8dece8d48ccce7d55afe7ce9a3d73c2427679d58db6260e25df7a9d4fbd91e77ad96e79d3a9e987e3a0cdd269a4cd6506bbeb2264201a8561272388f68453f54bc")
        );

        authService.getNode().addDependency(authDBHealthCheck);
        authService.getNode().addDependency(authDatabase);


        FargateService billingService = createFargateService(
                "BillingService",
                "billing-service",
                List.of(8084, 9090),
                null,
                null
        );

        FargateService analyticsService = createFargateService(
                "AnalyticsService",
                "analytics-service",
                List.of(8082),
                null,
                null
        );

        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createFargateService(
                "PatientService",
                "patient-service",
                List.of(8080),
                patientDatabase,
                Map.of(
                        "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                        "BILLING_SERVICE_GRPC_PORT", "9090"
                )
        );

        patientService.getNode().addDependency(patientDatabase);
        patientService.getNode().addDependency(patientDBHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);

    }

    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientVPC")
                .vpcName("PatientVPC")
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabaseInstance(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_4)
                                .build()
                ))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createDBHealthCheck(String id, DatabaseInstance db) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder
                .create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream()
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList())
                        )
                        .brokerAzDistribution("DEFAULT")
                        .build()
                )
                .build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder
                .create(this, "PatientCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient.local")
                        .build())
                .build();
    }

    private FargateService createFargateService(
            String id,
            String imageName,
            List<Integer> ports,
            DatabaseInstance db,
            Map<String, String> additionalEnvVars
    ) {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "Task")
                .cpu(254)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream()
                        .map(port -> PortMapping.builder()
                                .containerPort(port)
                                .build() ).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder
                                .create(this, id + "LogGorup")
                                .logGroupName("/ecs/" + imageName)
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .retention(RetentionDays.ONE_DAY)
                                .build())
                        .streamPrefix(imageName)
                        .build()));

        Map<String, String> envVars = new HashMap<>();

        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack:4511, localhost.localstack:4512");
        if(additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }

        if(db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-database".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }



        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());


        return FargateService.Builder
                .create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    public static void main(final String[] args){
        App app = new App(AppProps.builder()
                .outdir("./cdk.out")
                .build());

        StackProps stackProps = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", stackProps);
        app.synth();
        System.out.println("Stack is ready");

    }
}