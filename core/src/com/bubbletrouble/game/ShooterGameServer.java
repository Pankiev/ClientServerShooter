package com.bubbletrouble.game;

import java.io.IOException;

import com.bubbletrouble.game.kryonetcommon.IdProvider;
import com.bubbletrouble.game.kryonetcommon.PacketsRegisterer;
import com.bubbletrouble.game.kryonetcommon.Registerable;
import com.bubbletrouble.game.libgdxcommon.exception.GameException;
import com.bubbletrouble.game.packets.action.ActionInfo;
import com.bubbletrouble.game.packets.action.CollisionActionInfo;
import com.bubbletrouble.game.packets.database.DatabaseOperation;
import com.bubbletrouble.game.packets.produce.PlayerProduceInfo;
import com.bubbletrouble.game.packets.produce.ProduceInfo;
import com.bubbletrouble.game.packets.requsets.AddObstacleRequest;
import com.bubbletrouble.game.packets.requsets.DisconnectRequest;
import com.bubbletrouble.game.packets.requsets.Request;
import com.bubbletrouble.game.states.play.PlayServerState;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class ShooterGameServer extends ShooterGame
{
	public static final int tcpPort = 8000;
	public static final int udpPort = 8001;

	private Server server;
	private PlayServerState playState;

	@Override
	public void create()
	{
		super.create();
		server = new Server();
		registerPackets(server.getKryo());
		server.start();
		tryBindingServer();
		addListeners();
		playState = new PlayServerState(server);
		states.push(playState);
	}

	private void registerPackets(Kryo kryo)
	{
		PacketsRegisterer.registerAllAnnotated(kryo, Registerable.class, "com.bubbletrouble.game.objects");
		PacketsRegisterer.registerAllAnnotated(kryo, Registerable.class, "com.bubbletrouble.game.packets");
		PacketsRegisterer.registerDefaults(kryo);
	}

	private void tryBindingServer()
	{
		try
		{
			server.bind(tcpPort, udpPort);
		} catch (IOException e)
		{
			throw new ServerBindingExcepiton(e.getMessage());
		}
	}

	private void addListeners()
	{
		server.addListener(new ServerListener());
	}

	private void userConnected(Connection connection)
	{
		Integer id = connection.getID();
		server.sendToTCP(id, playState.getGameObjectsInfo());
		PlayerProduceInfo info = new PlayerProduceInfo(id);
		server.sendToAllExceptTCP(id, info);
		playState.addObject(info);

		Log.info(">> Player added " + connection.getID());
		playState.addMessage(">> Player added " + connection.getID());
		playState.addRandomObstacle();
	}

	private void userDisconnected(Connection connection)
	{
		if (playState.hasObject(connection.getID()))
			playState.removeObject(connection.getID());
		Log.info(">> Player disconnected " + connection.getID());
		playState.addMessage(">> Player disconnected " + connection.getID());
	}

	private void actionRecieved(ActionInfo actionInfo)
	{
		playState.makeAction(actionInfo);
		server.sendToAllTCP(actionInfo);
	}

	private void actionRecieved(CollisionActionInfo actionInfo)
	{
		playState.makeAction(actionInfo);
		server.sendToAllTCP(actionInfo);
	}

	private void produceInfoReceived(ProduceInfo produceInfo)
	{
		produceInfo.id = IdProvider.getNextId();
		playState.addObject(produceInfo);
		server.sendToAllTCP(produceInfo);
	}

	public void requestReceived(Request request)
	{
		request.perform(playState);
	}

	private void obstacleRequestReceived()
	{
		playState.addRandomObstacle();
	}

	private void performDatabaseOperation(DatabaseOperation operation)
	{		
		operation.perform();
	}

	private class ServerListener extends Listener
	{
		@Override
		public void connected(Connection connection)
		{
			userConnected(connection);
		}

		@Override
		public void disconnected(Connection connection)
		{
			userDisconnected(connection);
		}

		@Override
		public void received(Connection connection, Object object)
		{
			if (object instanceof ActionInfo)
				actionRecieved((ActionInfo) object);
			else if (object instanceof CollisionActionInfo)
				actionRecieved((CollisionActionInfo) object);
			else if (object instanceof ProduceInfo)
				produceInfoReceived((ProduceInfo) object);
			else if (object instanceof Request)
				requestReceived((Request) object);
			else if (object instanceof AddObstacleRequest)
				obstacleRequestReceived();
			else if (object instanceof DisconnectRequest)
				connection.close();
			else if (object instanceof DatabaseOperation)
				performDatabaseOperation((DatabaseOperation) object);
		}
	}

	private class ServerBindingExcepiton extends GameException
	{
		public ServerBindingExcepiton(String message)
		{
			super(message);
		}
	}

}
