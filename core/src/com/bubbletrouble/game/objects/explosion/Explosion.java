package com.bubbletrouble.game.objects.explosion;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.bubbletrouble.game.ShooterGame;
import com.bubbletrouble.game.libgdxcommon.State;
import com.bubbletrouble.game.libgdxcommon.camerashake.DefaultCameraShaker;
import com.bubbletrouble.game.libgdxcommon.objects.GameObject;
import com.bubbletrouble.game.packets.produce.ExplosionProduceInfo;
import com.bubbletrouble.game.packets.produce.ProduceInfo;
import com.bubbletrouble.game.states.interfaces.GameObjectsContainer;

public class Explosion extends GameObject
{
	private final Animation<TextureRegion> animation;
	private float livingTime = 0.0f;

	public Explosion(State linkedState)
	{
		super(ShooterGame.assets.get("ExplosionSet.png"), linkedState);
		Sound explosion = ShooterGame.assets.get("SpaceInvadersExplosion.wav");
		explosion.play();
		animation = createAnimation();
		shakeCamera();
	}

	private void shakeCamera()
	{
		DefaultCameraShaker shaker = new DefaultCameraShaker(linkedState.getCamera(), 10.0f, 0.5f);
		linkedState.shakeCamera(shaker);
	}

	private Animation<TextureRegion> createAnimation()
	{
		TextureRegion[][] textures = Sprite.split(ShooterGame.assets.get("ExplosionSet.png"), 32, 32);
		TextureRegion[] texturesTab = toOneDimenstionArray(textures);
		Animation<TextureRegion> animation = new Animation<>(0.016f, texturesTab);
		animation.setPlayMode(PlayMode.NORMAL);
		return animation;
	}

	private TextureRegion[] toOneDimenstionArray(TextureRegion[][] array)
	{
		List<TextureRegion> collection = new LinkedList<>();
		for (TextureRegion[] oneDimArray : array)
			for (TextureRegion object : oneDimArray)
				collection.add(object);
		TextureRegion[] newArray = new TextureRegion[collection.size()];
		return collection.toArray(newArray);
	}

	@Override
	public ProduceInfo produceInfo()
	{
		ExplosionProduceInfo info = new ExplosionProduceInfo();
		info.id = getId();
		info.position = new Vector2(getX(), getY());
		return info;
	}

	@Override
	public void clientUpdate()
	{
		livingTime += Gdx.graphics.getDeltaTime();
	}

	@Override
	public void serverUpdate()
	{
		livingTime += Gdx.graphics.getDeltaTime();
		if (animation.isAnimationFinished(livingTime))
			((GameObjectsContainer) linkedState).addToGarbage(this);
	}

	@Override
	public void render(SpriteBatch batch)
	{
		TextureRegion toRender = animation.getKeyFrame(livingTime);
		batch.draw(toRender, getX(), getY());
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

}
