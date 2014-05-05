#!/bin/sh
i="5";
ip_file=""

#file format class	id	vale1 value2 value3
java -cp target/twitter-naive-bayes-example-1.0-jar-with-dependencies.jar com.chimpler.example.bayes.TweetTSVToSeq ../../data_project/all_data_seq.txt ../../data_project/action-seq$i

#java -cp target/twitter-naive-bayes-example-1.0-jar-with-dependencies.jar com.chimpler.example.bayes.TweetTSVToSeq data/sample ../../data/heart-seq$i

hadoop fs -put ../../data_project/action-seq$i

mahout seq2sparse -i action-seq$i -o action-vec$i

mahout split -i action-vec$i/tfidf-vectors --trainingOutput action-train-vec$i --testOutput action-tst-vec$i --randomSelectionPct 40 --overwrite --sequenceFiles -xm sequential

mahout trainnb -i action-train-vec$i -el -li labelindex-action$i -o model-action$i -ow -c

mahout testnb -i action-tst-vec$i -l labelindex-action$i -m model-action$i -ow -o heart-action$i -c
echo "done"
echo heart-action$i






