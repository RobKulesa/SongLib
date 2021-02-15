package songlib.app.models;

public class SongModel {
    private String name;
	private String artist;
	private int year;
	private String album;
	
	public SongModel() {

	}

	public SongModel(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}

	public SongModel(String name, String artist, String album, int year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String s) throws IllegalArgumentException { 
		if(s.contains("|")) throw new IllegalArgumentException("Illegal input, \'|\' not allowed.");
		this.name = s.trim();
	}
	
	public String getArtist() {
		return this.artist;
	}

	public void setArtist(String s) { 
		if(s.contains("|")) throw new IllegalArgumentException("Illegal input, \'|\' not allowed.");
		this.artist = s.trim();
	}
	
	public String getAlbum() {
		return this.album;
	}

	public void setAlbum(String s) { 
		if(s.contains("|")) throw new IllegalArgumentException("Illegal input, \'|\' not allowed.");
		this.album = s;
	}
	
	public int getYear() {
		return this.year;
	}

	public void setYear(int n) {
		if(n < 0) throw new IllegalArgumentException("Illegal input, year must be at least 1.");
		this.year = n;
	}

	@Override
	public String toString() {
		return this.name + "," + this.artist;
	}
}
