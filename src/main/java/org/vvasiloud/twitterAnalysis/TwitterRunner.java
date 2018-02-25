package org.vvasiloud.twitterAnalysis;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.vvasiloud.twitterAnalysis.config.TwitterConfig;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

@Component
public class TwitterRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(TwitterRunner.class);
    private final TwitterConfig twitterConfig;

    @Autowired
    public TwitterRunner(TwitterConfig twitterConfig) {
        this.twitterConfig = twitterConfig;
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("Loading data...");
        test();

    }

    @Bean
    private SparkConf getSparkConf() {
        return new SparkConf()
                .setAppName("Tweets Android")
                .setSparkHome("~/Apps/spark-2.2.1-bin-hadoop2.7")
                .setMaster("local[*]");
    }

    @Bean
    private TwitterStream getTwitterStream() {
        return new TwitterStreamFactory(twitterConfig.getTwitterConfig().build()).getInstance();
    }

    private void getUser(JavaReceiverInputDStream<Status> stream) {
        // TODO
    }

    private void getTopUser(JavaReceiverInputDStream<Status> stream) {
        // TODO
    }

    private JavaDStream<String> getHashTags(JavaReceiverInputDStream<Status> stream) {
        return stream
                .flatMap((FlatMapFunction<Status, String>) s -> Arrays.asList(s.getText().split(" ")).iterator())
                .filter((Function<String, Boolean>) word -> word.startsWith("#"));

    }


    private void test() throws InterruptedException {
        TwitterStream twitterStream = getTwitterStream();
        SparkConf sparkConf = getSparkConf();
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(5000));

        String[] filters = {"#Android"};
        JavaReceiverInputDStream<Status> sparkTwitterStream = TwitterUtils.createStream(sc, twitterStream.getAuthorization(), filters);

        sparkTwitterStream
                .flatMap(s -> Arrays.asList(s.getHashtagEntities()).iterator())
                .map(h -> h.getText().toLowerCase())
                .filter(h -> !h.equals("android")).countByValue().print();

        sc.start();
        sc.awaitTermination();

    }
}
