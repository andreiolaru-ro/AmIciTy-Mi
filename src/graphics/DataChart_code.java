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
/*
 @SuppressWarnings("serial")
 class DataChart extends JPanel implements DistributionDrawer {
 Data[] data;
 XYSeries[] series;

 public DataChart(Data[] data) {
 this.data = data;
 XYSeriesCollection dataset = new XYSeriesCollection();
 series = new XYSeries[data.length];
 for (int i = 0; i < data.length; i++) {
 series[i] = new XYSeries("Data " + i);
 dataset.addSeries(series[i]);
 }
 JFreeChart chart = ChartFactory.createXYLineChart("title", "time",
 "value", dataset, PlotOrientation.VERTICAL, true, false,
 false);
 ChartPanel panel = new ChartPanel(chart);
 this.add(panel);
 }

 @Override
 public void updateDrawer() {
 int step = KCA.getStep();
 for (int id = 0; id < data.length; id++) {
 int count = 0;
 for (int i = 0; i < sy; i++) {
 for (int j = 0; j < sx; j++) {
 Collection<Fact> facts = cells.get(i).get(j).getFacts();
 boolean contains = false;
 for (Fact fact : facts) {
 if (fact.getDataRecursive().equals(data[id])) {
 contains = true;
 break;
 }
 }
 if (contains) {
 count++;
 }
 }
 }
 series[id].add(step, count);
 }
 }
 }
 */
