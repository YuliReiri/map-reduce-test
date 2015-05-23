package parser

import scalax.io._


// To solve this task we will use standard map / reduce aproach 
// so task should be treated as to count: number of response time entries and number of dynos entries by keys
// after when those valueas are counted we can apply mode, avarage and other functions to them
object ParseLogFile extends App {

  import collection.immutable._

  var dyno = List[ Map[String, Map[String, Int]]]()
  var response = List[ Map[String, Map[Int, Int]]]()

  // Open a file an read line by line 
  scala.io.Source.fromFile( args(0) ).getLines.foreach( line => {

    val mstr = map( line ) 

    // minimize mamory uses on operating only on the keys that are necessary by TDD
    if ( mstr._1 =="GET/api/count_pending_messages" ||  mstr._1 == "GET/api/get_messages" 
      || mstr._1 == "GET/api/get_friends_progress" || mstr._1 == "GET/api/get_friends_score"
      || mstr._1 == "POST/api" || mstr._1 == "GET/api" ) {

        // Map response time 
        response ::= Map( mstr._1 -> mstr._2 ) 
        // Map dyno 
        dyno ::= Map( mstr._1 -> mstr._3 )
    } 
  })
  def map( line: String ) = {
   
    // map incoming string to a param map 
    val params = line.split(" ").drop( 3 ).
      map( x => {val s = x.split("="); Map( s(0)->s(1) ) } ).reduce( _++_ )

    // cut users information from the request string 
    val path =  ".users/\\d+".r.replaceFirstIn( params.get("path").get, "" )

    // form aggregation key 
    val key: String = params.get("method").get + path

    // count response time
    val r = Map( (params.get("service").get.dropRight(2).toInt + 
      params.get("connect").get.dropRight(2).toInt) -> 1 )
  
    val d = Map( params.get("dyno").get -> 1 )

    ( key, r, d  )
  }


  //reduce response time 
  val res = reduce[Int]( response )( valuesSum[Int] ) 
  // reduce dyno time
  val dyn = reduce[String]( dyno )( valuesSum[String] )

  def valuesSum[T]( m1: Map[T, Int], m2: Map[T,Int] ) =
    (m1.keySet ++ m2.keySet ).map( (key:T) => {
      Map[T,Int](key -> (m1.get(key).getOrElse(0) + m2.get(key).getOrElse(0)))
    }).reduce( _++_ )

 
  def reduce[T]( data: List[ Map[ String, Map[T,Int]]])( addMap:( Map[T,Int], Map[T,Int]) => Map[T,Int] ) = {
    // Reduce two maps by building new united keyset
    // after we iterate via keyset, taking maps by key from two maps
    // and applying to them addMaps function 
    data.reduce( (m1,m2)=> {
      ( m2.keySet++m1.keySet).map( key =>{
        Map[String,Map[T,Int]](key -> addMap( m1.get( key ).getOrElse( Map[ T ,Int]() ) , m2.get( key ).getOrElse( Map[ T ,Int]() )))
      }).reduce(_++_)
    })
  }
  res.foreach( r => {
    val elCnt= getElementCnt( r._2 ) 
    println( s"""For the key: ${r._1}: 
        calls: $elCnt, me: ${median( r._2 )}, av: ${getElementSum( r._2 )/elCnt}, mo: ${getMode(r._2)},
        most serving dyno: ${getMostDyno( dyn.get(r._1).get )} 
        """)
  })

  def getMostDyno( m: Map[String,Int] ) = {
    val count = m.values.toList.sorted.last
    m.map( r => {
      if( r._2 == count ) Some( r._1 ) else None
    }).filter( _ != None ).map( _.get ).fold("")( (x,y) => {x+y+" "})
  }

  def getElementCnt( m: Map[Int, Int] ) = m.values.reduce( _+_ )

  def getElementSum( m : Map[ Int, Int ] ) = m.map( x =>{x._1 * x._2 } ).reduce(_+_)
  
  def getMode( m: Map[ Int, Int] ) = {
    val l = m.values.toList.sorted.last
    m.map( r => { 
      if ( r._2 == l )Some( r._1 ) else None  
    }).filter( _ != None ).map( _.get ).fold( "" )( (x,y)=>{x+y.toString + " "})
  }
  
  def getElement( 
    pos: Int, key: Iterator[Int], m: Map[Int, Int], current: Int = 0 ): Int = {
    
    val k = key.next
    val c = current + m.get( k ).get
    if ( pos <= c ) 
      k 
    else 
      getElement( pos, key, m, c ) 
  }

 def median( m: Map[ Int, Int ] ) = {
    val s = m.keySet.toList.sorted
    val cnt = getElementCnt( m )

    cnt % 2  match {
      case 1 => getElement( cnt / 2, s.iterator, m )
      case 0 => 
        (getElement( cnt/ 2, s.iterator, m  ) + 
          getElement( cnt/2 - 1, s.iterator, m )) / 2.0
    }
  }
}

