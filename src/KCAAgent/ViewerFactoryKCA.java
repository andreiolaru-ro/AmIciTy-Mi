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
package KCAAgent;

import java.awt.Color;
import java.util.Collection;

import logging.LogViewer;
import net.xqhs.windowLayout.rows.RowsLayout.RowsWindowParameters;
import KCAAgent.Goal.GoalType;
import KCAAgent.Logix.Domain;
import base.graphics.AbstractAggregGraphViewer;
import base.graphics.AbstractApproxGridViewer;
import base.graphics.AbstractAvgGraphViewer;
import base.graphics.AbstractGraphViewer.GraphParam;
import base.graphics.AbstractGridViewer;
import base.graphics.AbstractMaxGraphViewer;
import base.graphics.ControllableView;
import base.graphics.ViewerFactory;
import base.measure.MeasureName;

public class ViewerFactoryKCA extends ViewerFactory {
	public static ControllableView<EnvironmentKCA>[] createViewers(EnvironmentKCA cm,
			RowsWindowParameters params[]) {
		ControllableView<EnvironmentKCA>[] viewers = new ControllableView[params.length];
		RowsWindowParameters control = null;
		for (int i = 0; i < params.length && control == null; i++)
			if (params[i].getType().equals(Type.CONTROL.toString()))
				control = params[i];

		SimulationKCA kca = (SimulationKCA) (control.getData());
		kca.createMainWindow(control.getX(), control.getY(), control.getWidth(),
				control.getHeight());

		for (int i = 0; i < params.length; i++) {
			if (!params[i].getType().equals(Type.CONTROL.toString()))
				viewers[i] = (ControllableView<EnvironmentKCA>) createViewer(cm, params[i]);
		}

		return viewers;
	}

	public static ControllableView<?> createViewer(EnvironmentKCA cm, RowsWindowParameters params) {
		ControllableView<?> viewer = createViewerSub(cm, Type.valueOf(params.getType()),
				params.getData());
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

	private static ControllableView<EnvironmentKCA> createViewerSub(EnvironmentKCA cm, Type type,
			Object data) {
		switch (type) {
		// case SELECTED_AGENT_DETAILS:
		// return new
		// SelectedAgentDetails(cm).setTitle("Selected Agent Details");
		// case AGENT_DETAILS:
		// return new AgentDetails(cm).setTitle("Agent Details");
		case AGENT_SELECTION_GRID:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm) {
				@Override
				public Color getColor(KCAAgent cell) {
					return Color.GREEN;
				}

			}.setTitle("Selection Grid");
		case PAUSE_GRID:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm) {
				@Override
				public Color getColor(KCAAgent cell) {
					return cell.isPause() ? Color.red : Color.green;
				}
			}.setTitle("Pause Grid");
		case SPECIALTY_GRID:
			return new AbstractApproxGridViewer(cm, null, 30, 30) {
				@Override
				public Color getColor(KCAAgent cell) {
					return ((Specialty) cell.getMeasure(MeasureName.SPECIALTY)).getColor();
				}
			}.setTitle("Interest Grid - all");
		case PRESSURE_GRID:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm) {
				@Override
				public Color getColor(KCAAgent cell) {
					double pressure = ((Float) cell.getMeasure(MeasureName.AGENT_PRESSURE)
							.getValue()).doubleValue();
					if (pressure >= 0) {
						double pr = 1 - Math.pow(1 - pressure, 2); // make low
																	// pressure
																	// look
																	// slightly
																	// higher
						double c = 1 - pr; // complement
						return new Color(1.0f, (float) c, (float) c);
					}
					double pr = 1 - Math.pow(1 + pressure, 2); // make low
																// pressure look
																// slightly
																// higher
					double c = 1 - pr; // complement
					return new Color((float) c, (float) c, 1.0f);
				}
			}.setTitle("Pressure Grid");
		case DOMAIN_INTEREST_GRID:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm, data) {
				@Override
				public Color getColor(KCAAgent cell) {
					Domain domain = (Domain) data;
					float val = (float) ((Specialty) cell.getMeasure(MeasureName.SPECIALTY))
							.getValue(domain);
					float a = 1;
					float b = 1 - val;
					switch (domain) {
					case A:
						return new Color(a, b, b);
					case B:
						return new Color(b, a, b);
					case C:
						return new Color(b, b, a);
					default:
						return null;
					}
				}
			}.setTitle("Interest Grid - " + data);
			// case DATA_GRID:
			// return new AbstractGridViewer<EnvironmentKCA,KCAAgent>(cm, data)
			// {
			// @Override
			// public Color getColor(Agent cell)
			// {
			// Data d = (Data)data;
			// return cell.getData().contains(d) ? d.getSpecialty().getColor() :
			// Color.white;
			// }
			// }.setTitle("Data Grid - " + data);
		case FACTS_GRID:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm, data) {
				@Override
				public Color getColor(KCAAgent cell) {
					DataContent d = (DataContent) data;
					Collection<Fact> facts = cell.getFacts(false);
					Color agentColor = Color.white;
					for (Fact fact : facts) {
						if (fact.getAbstractContentRecursive().equals(d)) {
							if (fact.getAgentFactBalance() >= balanceMinimum) {
								agentColor = Color.red; // good
								break;
							}
							agentColor = Color.green; // bad
							/*
							 * if(Logix.similarity(cell.getSpecialty(),
							 * fact.getSpecialty())*
							 * cell.gradeFactHistory(fact.getSpecialty(),
							 * fact.firstStep) >= balanceMinimum)
							 * //if(Logix.similarity
							 * (cell.specialtyHistory[cell.currentSpecialty-1],
							 * fact.getSpecialty()) >= balanceMinimum) {
							 * agentColor = Color.red; break; } else agentColor
							 * = Color.green;
							 */
						}
					}
					return agentColor;
				}
			}.setTitle("Data Facts Grid - " + data);
			// case PRESSURE_SURFACE:
			// return new AbstractSurfaceViewer(cm) {
			// @Override
			// protected boolean isHighlighted(int x, int y)
			// {
			// return cm.cellAt(y, x).isSelected();
			// }
			//
			// @Override
			// protected double getHeight(int x, int y)
			// {
			// return cm.cellAt(y, x).getPressure();
			// }
			// }.setTitle("Pressure Surface");
			// case DATA_FACTS_SURFACE:
			// return new AbstractSurfaceViewer(cm, data) {
			// @Override
			// protected boolean isHighlighted(int x, int y)
			// {
			// return cm.cellAt(y, x).isSelected();
			// }
			//
			// @Override
			// protected double getHeight(int x, int y)
			// {
			// return Fact.filterCollectionOnData(cm.cellAt(x,
			// y).getFacts(false), (Data)data).size() / 100.0;
			// }
			// }.setTitle("Data Facts Surface - " + data);
		case GLOBAL_FACT_NUMBER_GRAPH:
			return new AbstractAggregGraphViewer(cm, data) {
				@Override
				public double getCellValue(KCAAgent cell) {
					if (data != null)
						return Fact.filterCollectionOnAbstractContent(cell.getFacts(false), data)
								.size();
					return cell.getFacts(false).size();
				}
			}.setTitle(data != null ? ((data instanceof GraphParam) ? ((GraphParam) data).getData()
					: data) + " - Facts Number" : "Total Facts Number");
		case GLOBAL_GOAL_NUMBER_GRAPH: // ca receive in data the GoalType to
										// filter goals by
			return new AbstractAggregGraphViewer(cm, data) {
				@Override
				public double getCellValue(KCAAgent cell) {
					if ((data != null) && (data instanceof GoalType))
						return Goal.filterCollectionOfType(cell.getGoals(), (GoalType) data).size();
					return cell.getGoals().size();
				}
			}.setTitle("Goal Number"
					+ (((data != null) && (data instanceof GoalType)) ? (" - " + ((GoalType) data)
							.toString()) : ""));
		case GLOBAL_PRESSURE_GRAPH:
			return new AbstractAggregGraphViewer(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell) {
					return (Float) cell.getMeasure(MeasureName.AGENT_PRESSURE).getValue();
				}

				@Override
				protected double stringScale() {
					return 1.0 / (cm.getAgents().size()) * 100;
				};

				@Override
				protected String makeString(double val, double max) {
					return (int) val + " : " + super.makeString(val, max);
				};
			}.setTitle("Pressure Average Graph");
		case MAX_PRESSURE_GRAPH:
			return new AbstractMaxGraphViewer<EnvironmentKCA, KCAAgent>(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell) {
					return (Float) cell.getMeasure(MeasureName.AGENT_PRESSURE).getValue();
				}

				@Override
				public double stringScale() {
					return 100;
				}
			}.setTitle("Max Agent Pressure");
		case MESSAGE_AVG_GRAPH:
			return new AbstractAvgGraphViewer(cm, data, Color.blue) {
				@Override
				public double getCellValue(KCAAgent cell) {
					return cell.getInbox().size();
				}

				@Override
				public double stringScale() {
					return 100;
				}
			}.setTitle("Message Number");
		case USELESS_FACTS_AVG_GRAF:
			return new AbstractAvgGraphViewer(cm, data, Color.red) {
				@Override
				public double getCellValue(KCAAgent cell) { // mean percentage

					/*
					 * Collection<Fact> facts = cell.getFacts(false); double n =
					 * facts.size(); double uselessFacts = 0; for(Fact fact :
					 * facts) { if(Logix.similarity(cell.getSpecialty(),
					 * fact.getSpecialty()) *
					 * cell.gradeFactHistory(fact.getSpecialty(),
					 * fact.firstStep) < balanceMinimum) uselessFacts++; } if(n
					 * > 0) return uselessFacts / n; return 0;
					 */
					return (Double) cell.getMeasure(MeasureName.AGENT_USELESS_FACT).getValue();
				}

				@Override
				public double stringScale() {
					return 100;
				}
			}.setTitle("Useless Facts Number");
		case AGENT_BALANCE:
			return new AbstractGridViewer<EnvironmentKCA, KCAAgent>(cm) {
				@Override
				public Color getColor(KCAAgent cell) { // more red -> higher
														// balance value -> good
					double sim = ((Double) cell.getMeasure(MeasureName.AGENT_BALANCE).getValue())
							.doubleValue();
					return new Color(1.0f, 1.0f - (float) sim, 1.0f - (float) sim);
				}
			}.setTitle("Agent Balance");
		case AGENT_BALANCE_AVG_GRAF:
			return new AbstractAvgGraphViewer(cm, data, Color.blue) {
				@Override
				public double getCellValue(KCAAgent cell) { // mean balance, in
															// percentage
					return (Double) cell.getMeasure(MeasureName.AGENT_BALANCE).getValue();
				}

				@Override
				public double stringScale() {
					return 100;
				}
			}.setTitle("Agent Balance");
		case LOG_VIEWER:
			return new LogViewer<EnvironmentKCA>(cm).setTitle("Event Log");
		case CONTROL:
			return null;
		default:
			return null;
		}
	}
}
