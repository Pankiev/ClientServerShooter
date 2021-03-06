package com.bubbletrouble.game.packets.produce;

import com.bubbletrouble.game.kryonetcommon.Info;
import com.bubbletrouble.game.kryonetcommon.Registerable;
import com.bubbletrouble.game.libgdxcommon.State;
import com.bubbletrouble.game.libgdxcommon.objects.GameObject;

@Registerable
public abstract class ProduceInfo extends Info
{
	public long id;

	public abstract GameObject produce(State linkedState);
}
