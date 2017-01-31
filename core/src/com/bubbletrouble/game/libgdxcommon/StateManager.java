package com.bubbletrouble.game.libgdxcommon;

import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StateManager
{
	private Stack<State> states = new Stack<State>();

	public void update()
	{
		usedState().update();
	}

	private State usedState()
	{
		return states.peek();
	}

	public void render(SpriteBatch batch)
	{
		usedState().render(batch);
	}

	public void set(State state)
	{
		states.pop();
		states.push(state);
	}

	public void push(State state)
	{
		states.push(state);
	}

	public void pop()
	{
		states.pop();
	}

	public boolean empty()
	{
		return states.empty();
	}

}