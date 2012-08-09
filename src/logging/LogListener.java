package logging;

public interface LogListener {
	void add(LogEntry entry);
	void remove(LogEntry entry);
}
