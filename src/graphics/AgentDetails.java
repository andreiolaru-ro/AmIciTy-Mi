package graphics;

import java.awt.Graphics2D;
import base.Environment;

public class AgentDetails extends AbstractViewer2D {
	@SuppressWarnings("hiding")
	public AgentDetails(Environment cm) {
		super(cm, null);
		setSize(600, 600);
	}
	
	@Override
	protected void draw(Graphics2D g) {
//		int padding = 3;
//		int w = (getWidth() - padding) / cm.sx - padding;
//		int h = (getHeight() - padding) / cm.sy - padding;
//		
//		for (int i = 0; i < cm.sx; i++) {
//			for (int j = 0; j < cm.sy; j++) {
//				drawCell(g, cm.cellAt(i, j), i * w + (i + 1) * padding, j * h + (j + 1) * padding, w, h);
//			}
//		}
	}

//	private static Color getColor(double pressure) {
//		if (pressure >= 0) {
//			double p = Math.min(pressure, 10) * 0.1;
//			double val = (1 - p) * 0.9;
//			return new Color(1.0f, (float) val, (float) val);
//		} else {
//			double p = Math.min(-pressure, 10) * 0.1;
//			double val = (1 - p) * 0.9;
//			return new Color((float) val, (float) val, 1.0f);
//		}
//	}
//	
//	protected static void drawCell(Graphics2D gg, Agent cell, int left, int top, int width, int height) {
//		int border = 3;
//		int padding = 3;
//		int w = width - 2 * border - 2 * padding;
//		int h = height - 2 * border - 2 * padding;
//		// interest
//		int ih = 5;
//		// messages
//		int mh = 5;
//		int mp = 2;
//		// data
//		int dw = (w - padding) / 2;
//		int dh = h - ih - 3 * mh - 3 * padding;
//		// facts
//		int fw = w - padding - dw;
//		int fh = dh;
//		
//		// draw border
//		//TODO set color according to pressure
//		gg.setColor(getColor((int)cell.getPressure()));
//		gg.fillRect(left, top, width, height);
//		gg.setColor(Color.black);
//		gg.fillRect(left + border, top + border, w + 2 * padding, h + 2 * padding);
//		int x = left + border + padding;
//		int y = top + border + padding;
//		
//		// draw interest
//		gg.setColor(cell.getSpecialty().getColor());
//		gg.fillRect(x, y, w, ih);
//		y += ih + padding;
//		
//		gg.setColor(Color.white);
//		gg.fillRect(x, y - padding, w, dh + 2 * padding);
//		
//		// draw data
//		gg.setColor(Color.black);
//		gg.fillRect(x, y, dw, dh);
//		int cp = 2;
//		if ((dw - cp) * (dh - cp) >= (cp + 1) * (cp + 1) * cell.getCapacity()) {
//			int cs = (int) Math.floor(Math.sqrt((dw - cp) * (dh - cp) / cell.getCapacity())) - cp;
//			int nc = (dw - cp) / (cs + cp);
//			int nr = (dh - cp) / (cs + cp);
//			while (nr * nc < cell.getCapacity()) {
//				cs--;
//				nc = (dw - cp) / (cs + cp);
//				nr = (dh - cp) / (cs + cp);
//			}
//			int cw = (dw - cp) / nc - cp;
//			int ch = (dh - cp) / nr - cp;
//			int cx = (dw - nc * cw - (nc - 1) * cp) / 2;
//			int cy = (dh - nr * ch - (nr - 1) * cp) / 2;
//			int k = 0;
//			for (Data data : cell.getData()) {
//				gg.setColor(data.getSpecialty().getColor());
//				for (int i = 0; i < data.getSize(); i++) {
//					int xx = x + cx + (k % nc) * (cw + cp);
//					int yy = y + cy + (k / nc) * (ch + cp);
//					gg.fillRect(xx, yy, cw, ch);
//					k++;
//				}
//			}
//		}
//		x += dw + padding;
//		
//		// draw facts
//		gg.setColor(Color.black);
//		gg.fillRect(x, y, fw, fh);
//		Map<Data, Collection<Fact>> facts = new HashMap<Data, Collection<Fact>>();
//		for (Fact fact : cell.getFacts(true)) {
//			if (facts.containsKey(fact.getDataRecursive())) {
//				facts.get(fact.getDataRecursive()).add(fact);
//			} else {
//				facts.put(fact.getDataRecursive(), fact.toCollection());
//			}
//		}
//		int fp = 2;
//		int fsize = facts.keySet().size();
//		if ((fw - fp) * (fh - fp) >= (fp + 1) * (fp + 1) * fsize) {
//			int fs = 0;
//			if(fsize > 0)
//				fs = (int) Math.floor(Math.sqrt((fw - fp) * (fh - fp) / fsize)) - fp;
//			int fc = (fw - fp) / (fs + fp);
//			int fr = (fh - fp) / (fs + fp);
//			while (fr * fc < fsize) {
//				fs--;
//				fc = (fw - fp) / (fs + fp);
//				fr = (fh - fp) / (fs + fp);
//			}
//			int fcw = (fw - fp) / fc - fp;
//			int fch = (fh - fp) / fr - fp;
//			int fx = (fw - fc * fcw - (fc - 1) * fp) / 2;
//			int fy = (fh - fr * fch - (fr - 1) * fp) / 2;
//			int k = 0;
//			for (Data data : facts.keySet()) {
//				int xx = x + fx + (k % fc) * (fcw + fp);
//				int yy = y + fy + (k / fc) * (fch + fp);
//				gg.setColor(data.getSpecialty().getColor());
//				gg.fillRect(xx, yy, fcw, fch);
//				int ph = 6; // pressure bar height
//				int count = 0;
//				int[] pr = new int[21];
//				for (Fact fact : facts.get(data)) {
//					pr[(int)(fact.getPressure()) + 10]++;
//					count++;
//				}
//				gg.setColor(Color.black);
//				gg.setFont(new Font(null, Font.BOLD, 12));
//				//TODO center the text and resize to fit the rectangle
//				gg.drawString(count + "", xx + padding, yy + fch - padding);
//				double xxx = 0;
//				for (int p = -10; p <= 10; p++) {
//					gg.setColor(getColor(p));
//					double ww = fcw * pr[p + 10] / (double) count;
//					gg.fillRect(xx + (int) xxx, yy, (int) (xxx + ww) - (int) xxx, ph);
//					xxx += ww;	
//				}
//				k++;
//			}
//		}
//		x = left + border + padding;
//		y += dh + padding;
//		
//		// draw messages
//		gg.setColor(Color.black);
//		gg.fillRect(x, y, w, mh);
//		int mw = (w + mp) / Math.max(10, cell.getInbox().size()) - mp;
//		for (Message msg : cell.getInbox()) {
//			Color color = Color.black;
//			switch (msg.getType()) {
//			case DATA :
//				color = Color.green;
//				break;
//			case INFORM :
//				color = Color.blue;
//				break;
//			case REQUEST :
//				color = Color.red;
//				break;
//			}
//			gg.setColor(color);
//			gg.fillRect(x, y, mw, mh);
//			x += mw + mp;
//		}
//		x = left + border + padding;
//		y += mh + padding;
//
//		// draw plans
//		gg.setColor(Color.black);
//		gg.fillRect(x, y, w, 2 * mh);
//		int pw = (w + mp) / Math.max(10, cell.getPlans().size() + 1) - mp;
//		Iterator<Intention> it = cell.getPlans().iterator();
//		if(it.hasNext())
//		{
//			Intention intention = it.next();
//			Goal g = intention.goal;
//			gg.setColor(getColor((int)g.getImportance()));
//			gg.fillRect(x, y, pw, mh);
//			if(g.getFact() != null)
//				gg.setColor(g.getFact().getDataRecursive().getSpecialty().getColor());
//			else
//				gg.setColor(g.getData().getSpecialty().getColor());
//			gg.fillRect(x, y + mh, pw, mh);
//			x += pw + mp;
//		}
//		x = left + border + padding + w - pw;
//		while(it.hasNext()) 
//		{
//			Intention intention = it.next();
//			Goal g = intention.goal;
//			gg.setColor(getColor((int)g.getImportance()));
//			gg.fillRect(x, y, pw, mh);
//			if(g.getFact() != null)
//				gg.setColor(g.getFact().getDataRecursive().getSpecialty().getColor());
//			else
//				gg.setColor(g.getData().getSpecialty().getColor());
//			gg.fillRect(x, y + mh, pw, mh);
//			x -= pw + mp;
//		}
//		x = left + border + padding;
//		y += mh + padding;
//	}
}
