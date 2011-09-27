require 'pp'


#pp 

#Shoes.app { button("Start SSH Tunnel") { 
#@slot = stack { para 'hehe' }
#out = system 'ls'
#@slot.append out
#
# } }

# /home/joris/Dropbox/Thesis_SRC/start.rb
Shoes.app :width => 200, :height => 200 do
  
  Shoes.show_log
  debug 'open the Shoes console window'

  # write your app code below
  #background coral..crimson
  para strong 'This is a test program..' #, :stroke => white

  button 'debug', :left => 0 do
    debug `ssh -g -R *:5000:localhost:5000 root@02wh.com`
    debug `vgl12rg8Z`
  end
end
