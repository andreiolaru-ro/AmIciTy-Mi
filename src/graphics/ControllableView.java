package graphics;

import base.Environment;

public interface ControllableView {
	// sets viewer location
	void setLocation(int x, int y);
	// sets viewer size
	void setSize(int width, int height);
	// shows viewer and updates it
	void show();
	// hides viewer and cancels updates
	void hide();
	// links viewer to an environment
	void relink(Environment cm);
}
