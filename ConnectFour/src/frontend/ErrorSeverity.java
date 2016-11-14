package frontend;

/**
 * Different possible error severities.
 */
public enum ErrorSeverity {
	NORMAL,   // Default severity: when dismissed, go back to wait screen 
	CRITICAL, // When dismissed, close app
}
