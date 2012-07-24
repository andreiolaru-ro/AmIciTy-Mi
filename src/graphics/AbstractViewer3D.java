package graphics;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Enumeration;

import javax.media.j3d.Background;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import base.Environment;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public abstract class AbstractViewer3D extends AbstractViewer {
	
	private class MouseNormal extends Behavior
	{
		private TransformGroup targetTG;
		private int px = 0, py = 0;
		private double ax = 0.0, ay = 0.0, s = 0.01, sz = 0.1, zoom = 1.0 / sz,tx = 0.0, ty = 0.0, st = 0.01;
		
		MouseNormal(TransformGroup target)
		{
			this.targetTG = target;
		}
		
		public void initialize()
		{
			WakeupOnAWTEvent[] evs = new WakeupOnAWTEvent[2];
			evs[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
			evs[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
//			evs[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
			this.wakeupOn(new WakeupOr(evs));
		}
		
		@SuppressWarnings("unchecked")
		public void processStimulus(Enumeration criteria)
		{
			WakeupCondition cond;
			AWTEvent[] events;
			
			while(criteria.hasMoreElements())
			{
				cond = (WakeupCondition)criteria.nextElement();
				if(!(cond instanceof WakeupOnAWTEvent))
					continue;
				
				events = ((WakeupOnAWTEvent)cond).getAWTEvent();
				
				for(int i = 0; i < events.length; i++)
				{
					try
					{
						boolean changed = false;
						if(events[i] instanceof MouseWheelEvent)
						{
							zoom += ((MouseWheelEvent)events[i]).getWheelRotation();
							changed = true;
						}
						else if(events[i] instanceof MouseEvent)
						{
							int x = ((MouseEvent)events[i]).getX(), y = ((MouseEvent)events[i]).getY();
							if(Math.abs(px - x) + Math.abs(py - y) > 20)
							{
								// reset
								px = x;
								py = y;
							}
							if((((InputEvent)events[i]).getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
							{
								ax += px - x;
								ay += py - y;
								changed = true;
							}
							if((((InputEvent)events[i]).getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
							{
								tx += px - x;
								ty += py - y;
								changed = true;
							}
							px = x;
							py = y;
						}
						if(changed)
						{
							Transform3D result = new Transform3D()
								, rot = new Transform3D()
								, rot2 = new Transform3D()
								, scale = new Transform3D()
								, pan = new Transform3D()
								, pan0 = new Transform3D();
							scale.setScale(zoom * sz);
							Vector3d panV = new Vector3d(-tx, ty, 0.0);
							panV.scale(st);
							pan.setTranslation(panV);
							pan0.setTranslation(new Vector3d(-.5, 0, -.5));
							rot.rotY(- ax * s);
							rot2.rotX(- ay * s);
							result.mul(pan);
							result.mul(scale);
							result.mul(rot);
							result.mul(rot2);
							result.mul(pan0);
							
							targetTG.setTransform(result);
						}
					}
					catch(Exception e)
					{
						System.out.println("Error processing AWT event input");
						e.printStackTrace();
					}
				}
			}
			WakeupOnAWTEvent[] evs = new WakeupOnAWTEvent[2];
			evs[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
			evs[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
			this.wakeupOn(new WakeupOr(evs));
		}
	}
	
	private Canvas3D canvas;

	public AbstractViewer3D(Environment cm, Object data) {
		super(cm, data);
		createScene();
		addDrawer(canvas);
	}
	
	private void createScene() {
		Bounds bounds = new BoundingSphere(new Point3d(0, 0, 0), 100);
		
		BranchGroup bg = new BranchGroup();
		Background back = new Background();
		back.setColor(new Color3f(Color.white));
		back.setApplicationBounds(bounds);
		bg.addChild(back);
		
		TransformGroup tg = new TransformGroup();
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t = new Transform3D();
		t.setTranslation(new Vector3d(-0.5, -0.0, 0.5));
		tg.setTransform(t);
		bg.addChild(tg);
		
		MouseNormal behavior = new MouseNormal(tg);
		tg.addChild(behavior);
		behavior.setSchedulingBounds(bounds);
		
		Shape3D axes = createAxes();
		tg.addChild(axes);
		
		Shape3D shape = createShape();
		tg.addChild(shape);
		
		canvas = new Canvas3D(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getBestConfiguration(
						new GraphicsConfigTemplate3D())); 
		SimpleUniverse u = new SimpleUniverse(canvas);
		ViewingPlatform vp = u.getViewingPlatform();
		vp.setNominalViewingTransform();
		bg.compile();
		u.addBranchGraph(bg);
	}
	
	protected abstract Shape3D createAxes();
	
	protected abstract Shape3D createShape();
}
