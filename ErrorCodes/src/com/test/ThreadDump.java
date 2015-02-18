package com.test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThreadDump {

	public static void main(String[] args) {
		new ThreadDump().dumpThreadInfoWithLocks();
	}

	private void dumpThreadInfoWithLocks() {
		ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] tinfos = tmbean.dumpAllThreads(true, true);
		StringBuffer sb = new StringBuffer();
		for (ThreadInfo ti : tinfos) {
			printThreadInfo(ti, sb);
		}
		System.out.println(sb.toString());
		System.out.println(getDeadlockInfo(tmbean));
	}

	private static final String INDENT = "    ";

	private void printThreadInfo(ThreadInfo ti, StringBuffer sb) {
		// print thread information
		printThread(ti, sb);

		// print stack trace with locks
		StackTraceElement[] stacktrace = ti.getStackTrace();
		MonitorInfo[] monitors = ti.getLockedMonitors();
		for (int i = 0; i < stacktrace.length; i++) {
			StackTraceElement ste = stacktrace[i];
			sb.append(INDENT + "at " + ste.toString());
			sb.append(getNewLineStr());
			for (MonitorInfo mi : monitors) {
				if (mi.getLockedStackDepth() == i) {
					sb.append(INDENT + "  - locked " + mi);
					sb.append(getNewLineStr());
				}
			}
		}
		sb.append(getNewLineStr());
		printLockInfo(ti.getLockedSynchronizers(), sb);
		sb.append(getNewLineStr());
	}

	private void printThread(ThreadInfo ti, StringBuffer sb) {
		sb.append("\"" + ti.getThreadName() + "\"" + " Id=" + ti.getThreadId()
				+ " in " + ti.getThreadState());
		if (ti.getLockName() != null) {
			sb.append(" on lock=" + ti.getLockName());
		}
		if (ti.isSuspended()) {
			sb.append(" (suspended)");
		}
		if (ti.isInNative()) {
			sb.append(" (running in native)");
		}
		sb.append(getNewLineStr());
		if (ti.getLockOwnerName() != null) {
			sb.append(INDENT + " owned by " + ti.getLockOwnerName() + " Id="
					+ ti.getLockOwnerId());
			sb.append(getNewLineStr());
		}
	}

	// default - JDK 6+ VM
	private String findDeadlocksMethodName = "findDeadlockedThreads";

	/**
	 * Checks if any threads are deadlocked. If any, get the thread dump
	 * information.
	 */
	public String getDeadlockInfo(ThreadMXBean tmbean) {
		long[] tids;
		StringBuffer sb = new StringBuffer();
		if (findDeadlocksMethodName.equals("findDeadlockedThreads")
				&& tmbean.isSynchronizerUsageSupported()) {
			tids = tmbean.findDeadlockedThreads();
			if (tids == null) {
				sb.append("No Deadlock!");
			} else {
				sb.append("Deadlock Found:");
				sb.append(getNewLineStr());
				ThreadInfo[] infos = tmbean.getThreadInfo(tids, true, true);
				for (ThreadInfo ti : infos) {
					printThreadInfo(ti, sb);
				}
			}

		} else {
			tids = tmbean.findMonitorDeadlockedThreads();
			if (tids == null) {
				sb.append("No Deadlock!");
			} else {
				sb.append("Deadlock Found:");
				sb.append(getNewLineStr());
				ThreadInfo[] infos = tmbean.getThreadInfo(tids,
						Integer.MAX_VALUE);
				for (ThreadInfo ti : infos) {
					// print thread information
					printThreadInfo(ti, sb);
				}
			}
		}

		return sb.toString();
	}

	private void printLockInfo(LockInfo[] locks, StringBuffer sb) {
		sb.append(INDENT + "Locked synchronizers: count = " + locks.length);
		sb.append(getNewLineStr());
		for (LockInfo li : locks) {
			sb.append(INDENT + "  - " + li);
			sb.append(getNewLineStr());
		}
		sb.append(getNewLineStr());
	}

	public String getThreadsInfo() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineStr());
		sb.append(getTabStr(2));
		sb.append("<jvm-thread-matrix>");
		sb.append(getNewLineStr());
		sb.append(getTabStr(3));
		sb.append("<peak-thread-count description=\"peak live thread count since the JVM started or peak was reset\">");
		sb.append(threadBean.getPeakThreadCount());
		sb.append("</peak-thread-count>");
		sb.append(getNewLineStr());
		sb.append(getTabStr(3));
		sb.append("<live-thread-count description=\"current number of live threads including both daemon and non-daemon threads\">");
		sb.append(threadBean.getThreadCount());
		sb.append("</live-thread-count>");
		sb.append(getNewLineStr());
		sb.append(getTabStr(3));
		sb.append("<thread-deadlock-count description=\"number of threads that are in deadlock waiting to acquire object monitors or ownable synchronizers\">");
		long threadCount = 0;
		if (threadBean.findDeadlockedThreads() != null)
			threadCount = threadBean.findDeadlockedThreads().length;
		sb.append(threadCount);
		sb.append("</thread-deadlock-count>");
		sb.append(getNewLineStr());
		sb.append(getTabStr(3));
		sb.append("<thread-dump description=\"current thread dump\">");
		sb.append("<![CDATA[");
		sb.append(getThreadsDump(threadBean));
		sb.append("]]>");
		sb.append("</thread-dump>");
		sb.append(getNewLineStr());
		sb.append(getTabStr(2));
		sb.append("</jvm-thread-matrix>");
		return sb.toString();
	}

	public static String getTabStr(int tab) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < tab; i++) {
			buf.append("\t");
		}
		return buf.toString();
	}

	public static String getNewLineStr() {
		return "\n";
	}

	private String getThreadsDump() {
		return getThreadsDump(ManagementFactory.getThreadMXBean());
	}

	private String getThreadsDump(ThreadMXBean threadBean) {
		ThreadInfo[] threadInfos = threadBean.dumpAllThreads(true, true);
		Map<Long, ThreadInfo> threadInfoMap = new HashMap<Long, ThreadInfo>();
		for (ThreadInfo threadInfo : threadInfos) {
			threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
		}
		StringWriter sw = new StringWriter();
		try {
			dumpTraces(threadBean, threadInfoMap, sw);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return sw.toString();
	}

	private static void dumpTraces(ThreadMXBean mxBean,
			Map<Long, ThreadInfo> threadInfoMap, Writer writer)
			throws IOException {
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		writer.write("Dump of "
				+ stacks.size()
				+ " thread at "
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z")
						.format(new Date(System.currentTimeMillis())) + "\n\n");
		for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
			Thread thread = entry.getKey();
			writer.write("\"" + thread.getName() + "\" prio="
					+ thread.getPriority() + " tid=" + thread.getId() + " "
					+ thread.getState() + " "
					+ (thread.isDaemon() ? "deamon" : "worker") + "\n");
			ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
			if (threadInfo != null) {
				writer.write("    native=" + threadInfo.isInNative()
						+ ", suspended=" + threadInfo.isSuspended()
						+ ", block=" + threadInfo.getBlockedCount() + ", wait="
						+ threadInfo.getWaitedCount() + "\n");
				writer.write("    lock="
						+ threadInfo.getLockName()
						+ " owned by "
						+ threadInfo.getLockOwnerName()
						+ " ("
						+ threadInfo.getLockOwnerId()
						+ "), cpu="
						+ (mxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L)
						+ ", user="
						+ (mxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L)
						+ "\n");
			}
			for (StackTraceElement element : entry.getValue()) {
				writer.write("        ");
				writer.write(element.toString());
				writer.write("\n");
			}
			writer.write("\n");
		}
	}

}
