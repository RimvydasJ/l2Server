package net.sf.l2j.gameserver.custom;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.PledgeCrest;

/**
 *
 * @author  Erlandys
 */
public class ImagesConverterManager
{
	protected static Logger _log = Logger.getLogger(ImagesConverterManager.class.getName());
	private static ImagesConverterManager instance = null;
	public static ImagesConverterManager getInstance()
	{
		if(instance == null)
			instance = new ImagesConverterManager();
		return instance;
	}
	private HashMap<String, HashMap<byte[], Integer>> images;

	public ImagesConverterManager()
	{
		images = new HashMap<String, HashMap<byte[], Integer>>();
		load();
	}

	public void load()
	{
		images.clear();
		long id = Integer.parseInt(String.valueOf(System.currentTimeMillis()).substring(4));
		final File mainDir = new File("./data/images");
		if (!mainDir.isDirectory())
		{
			_log.info("ImagesConverterManager: Main dir " + mainDir.getAbsolutePath() + " hasn't been found.");
			return;
		}
		
		for (final File file : mainDir.listFiles())
		{
			if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".bmp")))
				loadImage(file, ++id);
		}
		
		_log.info("ImagesConverterManager: Loaded " + images.size() + " images.");
	}

	private void loadImage(File file, long id)
	{		
		int imgId = (int)id;//IdFactory.getInstance().getNextId();
		try
		{
			byte data[] = DDSConverter.convertToDDS(file).array();
			HashMap<byte[], Integer> info = new HashMap<byte[], Integer>();
			info.put(data, imgId);
			String fileName = file.getName().substring(0, (file.getName().length() - 4 - (file.getName().endsWith(".jpeg") ? 1 : 0)));
			//System.out.println(fileName);
		    images.put(fileName, info);
		    updateImageForEveryone(imgId, data);
		}
		catch (Exception e)
		{
			_log.info(e.getMessage());
		}
	}

	public byte[] getImagePacket(String imageName)
	{
		if (images.containsKey(imageName))
			return (byte[])images.get(imageName).keySet().toArray()[0];
		
		return null;
	}

	public Integer getImageId(String imageName)
	{
		if (images.containsKey(imageName))
			return (Integer)images.get(imageName).values().toArray()[0];
		
		return 0;
	}

	private void updateImageForEveryone(int imageId, byte[] data)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.sendPacket(new PledgeCrest(imageId, data));
		}
	}

	public void updateAllImages(Player player)
	{
		for (int i = 0; i < images.size(); i++)	
			player.sendPacket(new PledgeCrest(getImageId((String)images.keySet().toArray()[i]), getImagePacket((String)images.keySet().toArray()[i])));
	}
}