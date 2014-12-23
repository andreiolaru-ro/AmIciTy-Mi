/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package P2PAgent;

import java.awt.Color;

import base.agent.AbstractAgent;
import base.graphics.AbstractGraphViewer;
import base.graphics.AbstractGridViewerWhitoutLocation;
import base.graphics.ControllableView;
import base.graphics.ViewerFactory;
import base.graphics.ViewerFactory.Type;
import base.graphics.ViewerFactory.WindowParameters;
import logging.LogViewer;

public class ViewerFactoryP2P {

	public static ControllableView<EnvironmentP2P>[] createViewers(EnvironmentP2P environment,
			WindowParameters params[]) {
		ControllableView<EnvironmentP2P>[] viewers = new ControllableView[params.length];
		WindowParameters control = null;
		for (int i = 0; i < params.length && control == null; i++)
			if (params[i].getType() == Type.CONTROL)
				control = params[i];

		SimulationP2P p2p = (SimulationP2P) (control.getData());
		p2p.createMainWindow(control.getX(), control.getY(), control.getWidth(),
				control.getHeight());

		for (int i = 0; i < params.length; i++) {
			if (params[i].getType() != Type.CONTROL)
				viewers[i] = createViewer(environment, params[i]);
		}

		return viewers;
	}

	public static ControllableView<EnvironmentP2P> createViewer(EnvironmentP2P environment,
			WindowParameters params) {
		ControllableView<EnvironmentP2P> viewer = createViewerSub(environment, params.getType(),
				params.getData(), params.getSpecific());
		viewer.setLocation(params.getX(), params.getY());
		if (params.getWidth() != 0 && params.getHeight() != 0) {
			// System.out.println("setting dim: " + params.width + ", " +
			// params.height);
			viewer.setSize(params.getWidth(), params.getHeight());
			// viewer.setSize(100, 100);
		}
		viewer.show();
		return viewer;
	}

	private static ControllableView<EnvironmentP2P> createViewerSub(EnvironmentP2P environment,
			Type type, Object data, Object specific) {
		switch (type) {
		case AGENT_SELECTION_GRID:
			return new AbstractGridViewerWhitoutLocation<EnvironmentP2P>(environment) {
				@Override
				public Color getColor(AbstractAgent cell) {
					return Color.GREEN;
				}
			}.setTitle("Selection Grid");
		case ITEM_GRAF:
			return new AbstractGraphViewer<EnvironmentP2P, P2PAgent>(environment, null, Color.red) {
				@Override
				public double getCellValue(P2PAgent cell) {
					return P2PAgent.getNumberItem().doubleValue();
				}

				@Override
				public double stringScale() {
					return 1;
				}

				@Override
				protected double calculateValue() {
					return P2PAgent.getNumberItem().doubleValue();
				}
			}.setTitle("Item owned");
		case ITEM_LOCATION_GRAF:
			return new AbstractGraphViewer<EnvironmentP2P, P2PAgent>(environment, null, Color.red) {
				@Override
				public double getCellValue(P2PAgent cell) {
					return P2PAgent.getNumberItem().doubleValue();
				}

				@Override
				public double stringScale() {
					return 1;
				}

				@Override
				protected double calculateValue() {
					return P2PAgent.getNumberItemLocation().doubleValue();
				}
			}.setTitle("Item location");
		case ITEM_WANTED_GRAF:
			return new AbstractGraphViewer<EnvironmentP2P, P2PAgent>(environment, null, Color.red) {
				@Override
				public double getCellValue(P2PAgent cell) {
					return P2PAgent.getNumberItem().doubleValue();
				}

				@Override
				public double stringScale() {
					return 1;
				}

				@Override
				protected double calculateValue() {
					return P2PAgent.getNumberItemWanted().doubleValue();
				}
			}.setTitle("Item wanted");

		case LOG_VIEWER:
			return new LogViewer<EnvironmentP2P>(environment).setTitle("Event Log");
		case CONTROL:
			return null;
		default:
			return null;
		}

	}

}
