package edu.umkc.ra.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import org.apache.log4j.BasicConfigurator;

/**
 * Created by Mayanka on 24-Sep-15.
 */
public class SentimentMain {
    public static void main(String args[]) {
        BasicConfigurator.configure();

        if (args != null && args.length > 0) {
            try {
                StormSubmitter.submitTopology(args[0], createConfig(false), createTopology());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count",createConfig(true),createTopology());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cluster.shutdown();
        }
    }


    private static StormTopology createTopology()
    {

        TopologyBuilder topology = new TopologyBuilder();


        topology.setSpout("edu.umkc.ra.storm.TwitterSpout", new TwitterSpout(), 4);

        topology.setBolt("SplitSentence", new SentimentBolt(), 4).shuffleGrouping("edu.umkc.ra.storm.TwitterSpout");




        return topology.createTopology();
    }

    private static Config createConfig(boolean local)
    {
        int workers = 1;
        Config conf = new Config();
        conf.setDebug(true);
        if (local)
            conf.setMaxTaskParallelism(workers);
        else
            conf.setNumWorkers(workers);
        return conf;
    }
}
