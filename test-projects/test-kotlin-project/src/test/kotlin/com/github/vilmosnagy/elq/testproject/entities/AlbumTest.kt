package com.github.vilmosnagy.elq.testproject.entities

import com.avaje.ebean.Ebean
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate
import com.github.vilmosnagy.elq.elqcore.stream.createElqStream
import com.github.vilmosnagy.elq.testproject.BaseTest
import org.junit.Assert.*
import java.util.stream.Collectors

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
class AlbumTest : BaseTest() {

    init {
        feature("Simple stream features (findAny, count, findFirst, findAll) shoould work") {

            scenario("should read some album entity from database") {
                val albums = server.createQuery(Album::class.java).findList()
                assertNotNull("Albums sucesfully load from database.", albums)
                assertNotEquals("More than zero albums loaded.", 0, albums.size)
            }


            scenario("should find any album entity trough stream") {
                assertNotNull("Some album sucesfully load from database.", createElqStream(Album::class.java).findFirst())
            }


            scenario("should count elements in table trough stream") {
                assertEquals("More than zero albums loaded.", 347, createElqStream(Album::class.java).count())
            }


            scenario("should find first album entity trough stream without ordering") {
                assertNotNull("Some album sucesfully load from database.", createElqStream(Album::class.java).findAny())
            }
        }

        feature("Filtering when primitive attribute equals to contstant should work") {

            scenario("filter with simple predicate where id equals constant should work correctly (id load compiled to iconts bytecode)") {
                val album = createElqStream(Album::class.java).filter { it.id == 5 }.findAny()

                if (album != null) {
                    assertEquals("Album found by ID with filter", "Big Ones", album.title)
                } else {
                    fail("Album not found by ID.")
                }
            }

            scenario("filter with simple predicate where id equals constant should work correctly (id load compiled to bipush bytecode)") {
                val album = createElqStream(Album::class.java).filter { it.id == 10 }.findAny()

                if (album != null) {
                    assertEquals("Album found by ID with filter", "Audioslave", album.title)
                } else {
                    fail("Album not found by ID.")
                }
            }

            scenario("filter with simple predicate where id equals constant should work correctly (id load compiled to sipush bytecode)") {
                val album = createElqStream(Album::class.java).filter { it.id == 347 }.findAny()

                if (album != null) {
                    assertEquals("Album found by ID with filter", "Koyaanisqatsi (Soundtrack from the Motion Picture)", album.title)
                } else {
                    fail("Album not found by ID.")
                }
            }

            scenario("filter with simple predicate where title equals should work correctly") {
                val album = createElqStream(Album::class.java).filter { it.title == "Big Ones" }.findAny()

                if (album != null) {
                    assertEquals("Album found by Title with filter", 5, album.id)
                } else {
                    fail("Album not found by title.")
                }
            }

            scenario("filter with simple predicate where title equals static method return value should work correctly (static method called with method local value)") {
                val album = createElqStream(Album::class.java).filter { it.title == java.lang.String.valueOf("Big Ones") }.findAny()

                if (album != null) {
                    assertEquals("Album found by Title with filter", 5, album.id)
                } else {
                    fail("Album not found by title.")
                }
            }

            scenario("filter with simple predicate where title equals static method return value should work correctly (static method called with final variable from parent class)") {
                val title = "Big Ones"
                val album = createElqStream(Album::class.java).filter { it.title == java.lang.String.valueOf(title) }.findAny()

                if (album != null) {
                    assertEquals("Album found by Title with filter", 5, album.id)
                } else {
                    fail("Album not found by title.")
                }
            }

            scenario("filter with simple predicate where id less than constant should work correctly") {
                val album = createElqStream(Album::class.java).filter { it.id < 5 }.collect(Collectors.toList())
                (album.count { it.title == "For Those About To Rock We Salute You" } == 1) shouldBe true
                (album.count { it.title == "Balls to the Wall" } == 1) shouldBe true
                (album.count { it.title == "Restless and Wild" } == 1) shouldBe true
                (album.count { it.title == "Let There Be Rock" } == 1) shouldBe true
                (album.all { it.id < 5 }) shouldBe true
                (album.size == 4) shouldBe true
            }

            scenario("filter with simple predicate where id less than or equal to constant should work correctly") {
                val album = createElqStream(Album::class.java).filter { it.id <= 5 }.collect(Collectors.toList())
                (album.count { it.title == "For Those About To Rock We Salute You" } == 1) shouldBe true
                (album.count { it.title == "Balls to the Wall" } == 1) shouldBe true
                (album.count { it.title == "Restless and Wild" } == 1) shouldBe true
                (album.count { it.title == "Let There Be Rock" } == 1) shouldBe true
                (album.count { it.title == "Big Ones" } == 1) shouldBe true
                (album.all { it.id <= 5 }) shouldBe true
                (album.size == 5) shouldBe true
            }

            scenario("filter with simple predicate where id greater than constant should work correctly") {
                val album = createElqStream(Album::class.java).filter { it.id > 343 }.collect(Collectors.toList())
                (album.count { it.title == "Schubert: The Late String Quartets & String Quintet (3 CD's)" } == 1) shouldBe true
                (album.count { it.title == "Monteverdi: L'Orfeo" } == 1) shouldBe true
                (album.count { it.title == "Mozart: Chamber Music" } == 1) shouldBe true
                (album.count { it.title == "Koyaanisqatsi (Soundtrack from the Motion Picture)" } == 1) shouldBe true
                (album.all { it.id > 343 }) shouldBe true
                (album.size == 4) shouldBe true
            }

            scenario("filter with simple predicate where id greater than or equal to constant should work correctly") {
                val album = createElqStream(Album::class.java).filter { it.id >= 343 }.collect(Collectors.toList())
                (album.count { it.title == "Schubert: The Late String Quartets & String Quintet (3 CD's)" } == 1) shouldBe true
                (album.count { it.title == "Monteverdi: L'Orfeo" } == 1) shouldBe true
                (album.count { it.title == "Mozart: Chamber Music" } == 1) shouldBe true
                (album.count { it.title == "Koyaanisqatsi (Soundtrack from the Motion Picture)" } == 1) shouldBe true
                (album.count { it.title == "Respighi:Pines of Rome" } == 1) shouldBe true
                (album.all { it.id >= 343 }) shouldBe true
                (album.size == 5) shouldBe true
            }
        }

        feature("Filtering when Many-To-One attribute equals to final variable should work") {
            scenario("Filter on ManyToOne with lambda") {
                val artist = Ebean.find<Artist>(Artist::class.java).where().eq("artistId", 1).findUnique()
                val expected = Ebean.find(Album::class.java).where().eq("artist", artist).findList()

                val albums = createElqStream(Album::class.java).filter { it -> it.artist == artist }.collect(Collectors.toList())

                assertEquals(expected, albums)
            }

            scenario("Filter on ManyToOne with anonym class") {
                val artist = Ebean.find<Artist>(Artist::class.java).where().eq("artistId", 1).findUnique()
                val expected = Ebean.find(Album::class.java).where().eq("artist", artist).findList()


                val albums = createElqStream(Album::class.java).filter(object : Predicate<Album> {
                    private fun doNotTransformThisBlockToLambdaExpression() {
                    }

                    override fun test(album: Album): Boolean? {
                        doNotTransformThisBlockToLambdaExpression()
                        return album.artist == artist
                    }
                }).collect(Collectors.toList())


                assertEquals(expected, albums)
            }
        }
    }
}