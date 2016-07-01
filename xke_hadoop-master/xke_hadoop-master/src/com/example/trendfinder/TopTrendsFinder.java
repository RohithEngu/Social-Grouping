package com.example.trendfinder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TopTrendsFinder extends Configured implements Tool {

	static String path [] = new String [3];
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        
        Job job1 = new Job(conf);
        job1.setJarByClass(TopTrendsFinder.class);       
        
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        
        job1.setMapperClass(TrendMapper1.class);
        job1.setCombinerClass(TrendReducer1.class);
        job1.setReducerClass(TrendReducer1.class);
        
        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);
       // job1.setSortComparatorClass(ReverseComparator.class);
        FileInputFormat.setInputPaths(job1, new Path(path[0]));
        FileOutputFormat.setOutputPath(job1, new Path(path[1]));

        boolean succ = job1.waitForCompletion(true);
        if (! succ) {
          System.out.println("Job1 failed, exiting");
          return -1;
        }
        
        
        //---------------------------------------------
        
        Job job2 = new Job(conf, "top-k-pass-2");
        job2.setJarByClass(TopTrendsFinder.class); 
        FileInputFormat.setInputPaths(job2, new Path(path[1]));
        FileOutputFormat.setOutputPath(job2, new Path(path[2]));
       
        job2.setMapperClass(TrendMapper2.class);
        job2.setReducerClass(TrendReducer2.class);
        
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setMapOutputKeyClass(LongWritable.class);
        job2.setMapOutputValueClass(Text.class);
        job2.setSortComparatorClass(LongWritable.DecreasingComparator.class);
        job2.setOutputFormatClass(TextOutputFormat.class);
       // job2.setNumReduceTasks(1);
        succ = job2.waitForCompletion(true);
        if (! succ) {
          System.out.println("Job2 failed, exiting");
          return -1;
        }
        
        return 0;
        
    }

    public static void main(String[] args) throws Exception {
    	
    	path[0] = args[0];
    	path[1] = args[1];
    	path[2] = args[2];
    	
        int res = ToolRunner.run(new TopTrendsFinder(), path);
        System.exit(res);
    }
}