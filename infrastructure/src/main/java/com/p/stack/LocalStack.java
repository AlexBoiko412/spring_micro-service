package com.p.stack;

import software.amazon.awscdk.*;
import java.util.HashMap;
import java.util.Map;

public class LocalStack extends Stack {

    public LocalStack(final App app, final String id, StackProps props) {
        super(app, id, props);
        software.amazon.awscdk.services.s3.Bucket.Builder.create(this, "MyBucket")
                .versioned(true)
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