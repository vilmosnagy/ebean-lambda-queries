package com.github.vilmosnagy.elq.testproject.entitytests;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.github.vilmosnagy.elq.elqcore.interfaces.Predicate;
import com.github.vilmosnagy.elq.elqcore.stream.ElqStreamImplKt;
import com.github.vilmosnagy.elq.testproject.BaseTest;
import com.github.vilmosnagy.elq.testproject.entites.Album;
import com.github.vilmosnagy.elq.testproject.entites.Artist;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
public class AlbumTest extends BaseTest {

    @Test
    public void should_read_some_album_entity_from_database() {
        List<Album> albums = server.createQuery(Album.class).findList();
        assertNotNull("Albums sucesfully load from database.", albums);
        assertNotEquals("More than zero albums loaded.", 0, albums.size());
    }

    @Test
    public void should_find_any_album_entity_trough_stream() {
        assertNotNull("Some album sucesfully load from database.", ElqStreamImplKt.createElqStream(Album.class).findFirst());
    }

    @Test
    public void should_count_elements_in_table_trough_stream() {
        assertEquals("More than zero albums loaded.", Long.valueOf(347), ElqStreamImplKt.createElqStream(Album.class).count());
    }

    @Test
    public void should_find_first_album_entity_trough_stream_without_ordering() {
        assertNotNull("Some album sucesfully load from database.", ElqStreamImplKt.createElqStream(Album.class).findAny());
    }

    @Test
    public void filter_with_simple_predicate_where_id_equals_constant_should_work_correctly_when_iload_generated() {
        Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() == 5).findAny();

        if (album != null) {
            assertEquals("Album found by ID with filter", "Big Ones", album.getTitle());
        } else {
            fail("Album not found by ID.");
        }
    }

    @Test
    public void filter_with_simple_predicate_where_id_equals_constant_should_work_correctly_when_bipush_generated() {
        Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() == 10).findAny();

        if (album != null) {
            assertEquals("Album found by ID with filter", "Audioslave", album.getTitle());
        } else {
            fail("Album not found by ID.");
        }
    }

    @Test
    public void filter_with_simple_predicate_where_id_equals_constant_should_work_correctly_when_sipush_generated() {
        Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() == 347).findAny();

        if (album != null) {
            assertEquals("Album found by ID with filter", "Koyaanisqatsi (Soundtrack from the Motion Picture)", album.getTitle());
        } else {
            fail("Album not found by ID.");
        }
    }

    @Test
    public void filter_with_simple_predicate_where_title_equals_constant_should_work_correctly() {
        Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getTitle().equals("Big Ones")).findAny();

        if (album != null) {
            assertEquals("Album found by Title with filter", 5, album.getId());
        } else {
            fail("Album not found by ID.");
        }
    }

    @Test
    // todo
    public void one_to_many_filters_should_work_when_equals_called_on_attribute_with_lambda() {
        final Artist artist = Ebean.find(Artist.class).where().eq("artistId", 1).findUnique();
        final List<Album> expected = Ebean.find(Album.class).where().eq("artist", artist).findList();

        final List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getArtist().equals(artist)).collect(Collectors.toList());

        assertEquals(expected, albums);
    }

    @Test
    public void one_to_many_filters_should_work_when_equals_called_on_attribute_with_anonym_class() {
        final Artist artist = Ebean.find(Artist.class).where().eq("artistId", 1).findUnique();
        final List<Album> expected = Ebean.find(Album.class).where().eq("artist", artist).findList();


        final List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(new Predicate<Album>() {
            private void doNotTransformThisBlockToLambdaExpression() {
            }

            ;

            @Override
            public Boolean test(Album album) {
                doNotTransformThisBlockToLambdaExpression();
                return album.getArtist().equals(artist);
            }
        }).collect(Collectors.toList());


        assertEquals(expected, albums);
    }

    @Test
    public void filter_with_simple_predicate_where_title_equals_static_method_return_value_should_work_correctly__static_method_called_with_method_local_value() {
        final Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getTitle().equals(String.valueOf("Big Ones"))).findAny();

        if (album != null) {
            assertEquals("Album found by Title with filter", 5, album.getId());
        } else {
            fail("Album not found by title.");
        }
    }

    @Test
    public void filter_with_simple_predicate_where_title_equals_static_method_return_value_should_work_correctly__static_method_called_final_variable_from_parent_class() {
        final String title = "Big Ones";
        final Album album = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getTitle().equals(String.valueOf(title))).findAny();

        if (album != null) {
            assertEquals("Album found by Title with filter", 5, album.getId());
        } else {
            fail("Album not found by title.");
        }
    }

    @Test
    public void filter_with_simple_predicate_where_id_less_than_constant_should_work_correctly() {
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() < 5).collect(Collectors.toList());
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("For Those About To Rock We Salute You")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Balls to the Wall")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Restless and Wild")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Let There Be Rock")).count() == 1);
        assertTrue(albums.stream().allMatch(it -> it.getId() < 5));
        assertTrue(albums.size() == 4);
    }

    @Test
    public void filter_with_simple_predicate_where_id_less_than_or_equal_to_constant_should_work_correctly() {
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() <= 5).collect(Collectors.toList());
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("For Those About To Rock We Salute You")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Balls to the Wall")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Restless and Wild")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Let There Be Rock")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Big Ones")).count() == 1);
        assertTrue(albums.stream().allMatch(it -> it.getId() <= 5));
        assertTrue(albums.size() == 5);
    }

    @Test
    public void filter_with_simple_predicate_where_id_greater_than_constant_should_work_correctly() {
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() > 343).collect(Collectors.toList());
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Schubert: The Late String Quartets & String Quintet (3 CD's)")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Monteverdi: L'Orfeo")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Mozart: Chamber Music")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Koyaanisqatsi (Soundtrack from the Motion Picture)")).count() == 1);
        assertTrue(albums.stream().allMatch(it -> it.getId() > 343));
        assertTrue(albums.size() == 4);
    }

    @Test
    public void filter_with_simple_predicate_where_id_greater_than_or_equal_to_constant_should_work_correctly() {
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getId() >= 343).collect(Collectors.toList());
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Schubert: The Late String Quartets & String Quintet (3 CD's)")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Monteverdi: L'Orfeo")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Mozart: Chamber Music")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Koyaanisqatsi (Soundtrack from the Motion Picture)")).count() == 1);
        assertTrue(albums.stream().filter(it -> it.getTitle().equals("Respighi:Pines of Rome")).count() == 1);
        assertTrue(albums.stream().allMatch(it -> it.getId() >= 343));
        assertTrue(albums.size() == 5);
    }

    @Test
    public void multiple_logical_expressions_connected_by_logical_and_should_be_parsed() {
        List<Album> expectedAlbums = Ebean.find(Album.class).where().ge("id", 343).eq("title", "Monteverdi: L'Orfeo").findList();
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class)
                .filter(it -> it.getId() >= 343 && it.getTitle().equals("Monteverdi: L'Orfeo"))
                .collect(Collectors.toList());
        assertEquals(expectedAlbums, albums);
    }

    @Test
    public void multiple_logical_expressions_connected_by_logical_or_should_be_parsed() {
        List<Album> expectedAlbums = Ebean.find(Album.class).where(Expr.or(
                Expr.eq("id", 343),
                Expr.eq("title", "Monteverdi: L'Orfeo")
        )).findList();

        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class)
                .filter(it -> it.getId() == 343 || it.getTitle().equals("Monteverdi: L'Orfeo"))
                .collect(Collectors.toList());

        assertEquals(expectedAlbums, albums);
    }

    @Test
    public void filter_trough_ManyToOnes_connection_on_id_field() {
        List<Album> expectedAlbums = Ebean.find(Album.class).where().eq("artist.id", 1).findList();
        List<Album> albums = ElqStreamImplKt.createElqStream(Album.class).filter(it -> it.getArtist().getId() == 1).collect(Collectors.toList());
        assertEquals(expectedAlbums, albums);
    }
}

