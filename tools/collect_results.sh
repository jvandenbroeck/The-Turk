# setup
rrlserver=vielsalm
aceserver=lanaken
rndserver=borgworm
# collect here

scp -r s0168656@$rrlserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/visits.log ./visits_rrl.log
scp -r s0168656@$rrlserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/run.log ./run_rrl.log
scp -r s0168656@$rrlserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/benchmark.xml ./benchmark.xml
scp -r s0168656@$rrlserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/plot/ .
scp -r s0168656@$rndserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/run.log ./run_random.log
scp -r s0168656@$rndserver.cs.kotnet.kuleuven.be:/tmp/Thesis-RRL-MCTS/visits.log ./visits_random.log
scp -r s0168656@$aceserver.cs.kotnet.kuleuven.be:/tmp/ace/time.log .

