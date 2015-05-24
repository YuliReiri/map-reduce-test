
This test was built with scala2.10. To execute this programm you should have java 1.8 installed.
To run it from command line under Ubuntu \ Mac please use: 
    ./parse.sh filename ( example: ./parse sample.log 
To be able to operate on src files in command line, you should have maven and scala installed: 
   * build: mvn compile 
   * execute: mvn exec:java
   * rut tests: mvn test
please notice this example doesn't contains check of incorrect data or it format.

Output results on the provided log file:

yuli_m@Yuli-MacBookPro:~/work/workspace/log-file-parser$ ./parse.sh sample.log 

For the key: GET/api/get_friends_score: 
        calls: 1533, me: 142.0, av: 228, mo: 67 ,
        most serving dyno: web.7  
        
For the key: POST/api: 
        calls: 2022, me: 46.0, av: 82, mo: 23 ,
        most serving dyno: web.11  
        
For the key: GET/api/get_friends_progress: 
        calls: 1117, me: 51.0, av: 111, mo: 35 ,
        most serving dyno: web.5  
        
For the key: GET/api/count_pending_messages: 
        calls: 2430, me: 15.0, av: 25, mo: 11 ,
        most serving dyno: web.2  
        
For the key: GET/api/get_messages: 
        calls: 652, me: 32.0, av: 62, mo: 23 ,
        most serving dyno: web.11  

