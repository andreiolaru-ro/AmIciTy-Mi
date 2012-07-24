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