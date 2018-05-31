package com.elfocrash.roboto.task;

import com.elfocrash.roboto.FakePlayer;
import com.elfocrash.roboto.FakePlayerTaskManager;

import java.util.Collections;
import java.util.List;

import net.sf.l2j.commons.concurrent.ThreadPool;

/**
 * @author Elfocrash
 *
 */
public class AITaskRunner implements Runnable
{
	@Override
	public void run()
	{		
		FakePlayerTaskManager.INSTANCE.adjustTaskSize();
		List<AITask> aiTasks = FakePlayerTaskManager.INSTANCE.getAITasks();		
		aiTasks.forEach(aiTask -> ThreadPool.execute(aiTask));
	}	
}