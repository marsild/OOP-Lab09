package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();
    private double max = Double.MIN_VALUE;
    private String longest;
    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                .map(s -> s.getSongName())
                .sorted(String::compareTo);
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.keySet().stream()
                .filter(s -> this.albums.get(s).equals(year));
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
                .filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().get().equals(albumName))
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                .filter(s -> s.getAlbumName().isEmpty())
                .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        final Optional<Double> sumTime = this.songs.stream()
                .filter(s -> s.getAlbumName().isPresent())
                .filter(s -> s.getAlbumName().get().equals(albumName))
                .map(s -> s.getDuration())
                .reduce((a, b) -> a + b);
        final int nSongs = this.countSongs(albumName);
        if (sumTime.isPresent()) {
            return OptionalDouble.of(sumTime.get() / nSongs);
        } else {
            return OptionalDouble.empty();
        }
    }

    @Override
    public Optional<String> longestSong() {
        final Optional<Song> maxSong = this.songs.stream()
                .max((s, s1) -> Double.compare(s.getDuration(), s1.getDuration()));
        if (maxSong.isPresent()) {
            return Optional.of(maxSong.get().getSongName());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> longestAlbum() {
        albums.keySet().forEach(album -> {
            final int nSongs = this.countSongs(album);
            final OptionalDouble average = this.averageDurationOfSongs(album);
            if (average.isPresent()) {
                final double product = average.getAsDouble() * nSongs;
                if (product > this.max) {
                    this.max = product;
                    this.longest = album;
                }
            }
        });
        return Optional.of(this.longest);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
