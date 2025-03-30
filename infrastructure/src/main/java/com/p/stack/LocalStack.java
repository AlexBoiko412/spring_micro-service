package com.p.stack;


import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.Vpc;

public class LocalStack extends Stack {
    private final Vpc vpc;

    public LocalStack(
            final App app,
            final String id,
            StackProps props
    ) {
        super(app, id, props);

        this.vpc = createVpc();
    }

    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "PatientVPC")
                .vpcName("PatientVPC")
                .maxAzs(2)
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