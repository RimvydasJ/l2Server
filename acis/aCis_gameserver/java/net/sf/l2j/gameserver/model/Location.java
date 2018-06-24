package net.sf.l2j.gameserver.model;

/**
 * Deedlit: we are using volatile variable types here. We dont need to additionally use synchronized, cause volatile vars are synced vars.
 */
public class Location
{
	public static final Location DUMMY_LOC = new Location(0, 0, 0,0);
	
	protected volatile int _x;
	protected volatile int _y;
	protected volatile int _z;
	protected volatile int _factionId;

	public Location(int x, int y, int z, int factionId)
	{
		_x = x;
		_y = y;
		_z = z;
		_factionId =factionId;
	}

	public Location(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
		_factionId =0;
	}
	
	public Location(Location loc)
	{
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
		_factionId = loc.getFactionId();
	}
	
	@Override
	public String toString()
	{
		return _x + ", " + _y + ", " + _z;
	}
	
	@Override
	public int hashCode()
	{
		return _x ^ _y ^ _z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Location)
		{
			Location loc = (Location) o;
			return (loc.getX() == _x && loc.getY() == _y && loc.getZ() == _z && _factionId == loc.getFactionId());
		}
		
		return false;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public int getZ()
	{
		return _z;
	}

	public int getFactionId(){return _factionId;}

	public void set(int x, int y, int z, int factionId)
	{
		_x = x;
		_y = y;
		_z = z;
		_factionId = factionId;
	}

	public void set(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
		_factionId = 0;
	}
	
	public void set(Location loc)
	{
		_x = loc.getX();
		_y = loc.getY();
		_z = loc.getZ();
		_factionId = loc._factionId;
	}
	
	public void clean()
	{
		_x = 0;
		_y = 0;
		_z = 0;
		_factionId = 0;
	}
}