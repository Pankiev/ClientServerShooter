package com.bubbletrouble.game.packets.remove;

import com.bubbletrouble.game.kryonetcommon.Info;
import com.bubbletrouble.game.kryonetcommon.Registerable;

@Registerable
public class ObjectRemoveInfo extends Info
{
	public long id = -1;

	public ObjectRemoveInfo()
	{

	}

	public ObjectRemoveInfo(long id)
	{
		this.id = id;
	}

}
