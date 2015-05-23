
import org.scalatest._
import parser.ParseLogFile._


class ParserTestSpecs extends FlatSpec with Matchers {

  val t = Map( 1 -> 2, 2 -> 3, 3 -> 4, 4 -> 5)
  
  

  val tLine = """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/100005058430446/get_messages host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.7 connect=2ms service=41ms status=200 bytes=451
"""  
  
  val lines = List( """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/100005058430446/get_messages host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.7 connect=2ms service=41ms status=200 bytes=451
""",  """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/058430446/get_messages host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.7 connect=8ms service=21ms status=200 bytes=451
""",  """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/10000430446/get_messages host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.10 connect=3ms service=31ms status=200 bytes=451
""",  """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/58430446/get_messages host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.8 connect=4ms service=30ms status=200 bytes=451
""",  """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/058430446/get_friends_progress host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.7 connect=8ms service=22ms status=200 bytes=451
""",   """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/10000430446/get_friends_progress host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.8 connect=2ms service=28ms status=200 bytes=451
""",  """
2014-01-09T06:18:06.917486+00:00 heroku[router]: at=info method=GET path=/api/users/58430446/get_friends_progress host=services.pocketplaylab.com fwd="110.77.164.97" dyno=web.8 connect=10ms service=20ms status=200 bytes=451
"""  )

  "getElementCnt" should "calculates elements count in Map[Int,Int]" in {
     
      getElementCnt( t ) shouldBe 14
  }
  "getElementSum" should "culculates elements sum in Map[Int,Int]"  in {
    getElementSum( t ) shouldBe 40
  }
  "median" should "calculates median" in {
    median(t) shouldBe 3
  }

  "getMode" should "calculates mode" in {
    getMode( t ) shouldBe "4 " 
  }
  "getElement" should "return element from Map[Int, Int]" in {
    getElement( 4, t.keySet.toList.sorted.iterator, t ) shouldBe 2 
  }
  "getMostDyno" should "finds most serving dyno" in {
    getMostDyno( Map( "w1" -> 8, "w9" -> 3, "w4" ->14, "w18" ->4 )) should be( "w4 ")
  }

  "map" should "map line to touple3 with key and two maps as values" in {
    val m = map( tLine ) 

    m._1 shouldBe  "GET/api/get_messages"
    m._2 shouldBe Map( 43 -> 1)
    m._3 shouldBe Map( "web.7" ->1 )
  }

  "reduce" should "reduce mapped results " in {

    var mr = lines.map( line =>{
      val ml = map( line )
      Map(ml._1 -> ml._2)
    })  

    val reduced = reduce[Int]( mr )( valuesSum )

    reduced shouldBe Map( 
      "GET/api/get_friends_progress" -> Map(30 -> 3),
      "GET/api/get_messages" -> Map( 43 -> 1, 29 -> 1, 34 ->2))

  }
}
