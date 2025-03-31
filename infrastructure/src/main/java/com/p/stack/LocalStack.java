package com.p.stack;


import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.stream.Collectors;

public class LocalStack extends Stack {
    private final Vpc vpc;

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