/**
 * Rutgers CS213 Sp21 SongLib (Asst1) 
 * @author Rob Kulesa
 * @author Aaron Kan
 */
package songlib.app.models;

public class SongModel implements Comparable<SongModel> {
    private String name;
	private String artist;
	private int year;
	private String album;
	
	public SongModel() {
		this.name = "";
		this.artist = "";
		this.album = "";
		this.year = 0;
	}

	public SongModel(String name, String artist) {
		this.name = name.trim();
		this.artist = artist.trim();
		this.album = "";
		this.year = 0;
	}

	public SongModel(String name, String artist, String album) {
		this.name = name.trim();
		this.artist = artist.trim();
		this.album = album.trim();
		this.year = 0;
	}

	public SongModel(String name, String artist, int year) {
		this.name = name.trim();
		this.artist = artist.trim();
		this.album = "";
		this.year = year;
	}

	public SongModel(String name, String artist, String album, int year) {
		this.name = name.trim();
		this.artist = artist.trim();
		this.album = album.trim();
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
		this.album = s.trim();
	}
	
	public int getYear() {
		return this.year;
	}

	public void setYear(int n) {
		if(n < 1) throw new IllegalArgumentException("Illegal input, year must be at least 1.");
		this.year = n;
	}

	@Override
	public String toString() {
		return this.name + ", " + this.artist;
	}

	@Override
	public int compareTo(SongModel b) {
		if(this.name.equalsIgnoreCase(b.name)) {
			return this.artist.compareToIgnoreCase(b.artist);
		}
		return this.name.compareToIgnoreCase(b.name);
	}
}
