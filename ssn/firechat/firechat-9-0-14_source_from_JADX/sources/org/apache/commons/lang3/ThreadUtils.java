package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ThreadUtils {
    public static final AlwaysTruePredicate ALWAYS_TRUE_PREDICATE = new AlwaysTruePredicate();

    private static final class AlwaysTruePredicate implements ThreadPredicate, ThreadGroupPredicate {
        public boolean test(Thread thread) {
            return true;
        }

        public boolean test(ThreadGroup threadGroup) {
            return true;
        }

        private AlwaysTruePredicate() {
        }
    }

    public static class NamePredicate implements ThreadPredicate, ThreadGroupPredicate {
        private final String name;

        public NamePredicate(String str) {
            if (str == null) {
                throw new IllegalArgumentException("The name must not be null");
            }
            this.name = str;
        }

        public boolean test(ThreadGroup threadGroup) {
            return threadGroup != null && threadGroup.getName().equals(this.name);
        }

        public boolean test(Thread thread) {
            return thread != null && thread.getName().equals(this.name);
        }
    }

    public interface ThreadGroupPredicate {
        boolean test(ThreadGroup threadGroup);
    }

    public static class ThreadIdPredicate implements ThreadPredicate {
        private final long threadId;

        public ThreadIdPredicate(long j) {
            if (j <= 0) {
                throw new IllegalArgumentException("The thread id must be greater than zero");
            }
            this.threadId = j;
        }

        public boolean test(Thread thread) {
            return thread != null && thread.getId() == this.threadId;
        }
    }

    public interface ThreadPredicate {
        boolean test(Thread thread);
    }

    public static Thread findThreadById(long j, ThreadGroup threadGroup) {
        if (threadGroup == null) {
            throw new IllegalArgumentException("The thread group must not be null");
        }
        Thread findThreadById = findThreadById(j);
        if (findThreadById == null || !threadGroup.equals(findThreadById.getThreadGroup())) {
            return null;
        }
        return findThreadById;
    }

    public static Thread findThreadById(long j, String str) {
        if (str == null) {
            throw new IllegalArgumentException("The thread group name must not be null");
        }
        Thread findThreadById = findThreadById(j);
        if (findThreadById == null || findThreadById.getThreadGroup() == null || !findThreadById.getThreadGroup().getName().equals(str)) {
            return null;
        }
        return findThreadById;
    }

    public static Collection<Thread> findThreadsByName(String str, ThreadGroup threadGroup) {
        return findThreads(threadGroup, false, new NamePredicate(str));
    }

    public static Collection<Thread> findThreadsByName(String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("The thread name must not be null");
        } else if (str2 == null) {
            throw new IllegalArgumentException("The thread group name must not be null");
        } else {
            Collection<ThreadGroup> findThreadGroups = findThreadGroups(new NamePredicate(str2));
            if (findThreadGroups.isEmpty()) {
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            NamePredicate namePredicate = new NamePredicate(str);
            for (ThreadGroup findThreads : findThreadGroups) {
                arrayList.addAll(findThreads(findThreads, false, namePredicate));
            }
            return Collections.unmodifiableCollection(arrayList);
        }
    }

    public static Collection<ThreadGroup> findThreadGroupsByName(String str) {
        return findThreadGroups(new NamePredicate(str));
    }

    public static Collection<ThreadGroup> getAllThreadGroups() {
        return findThreadGroups(ALWAYS_TRUE_PREDICATE);
    }

    public static ThreadGroup getSystemThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        return threadGroup;
    }

    public static Collection<Thread> getAllThreads() {
        return findThreads(ALWAYS_TRUE_PREDICATE);
    }

    public static Collection<Thread> findThreadsByName(String str) {
        return findThreads(new NamePredicate(str));
    }

    public static Thread findThreadById(long j) {
        Collection findThreads = findThreads(new ThreadIdPredicate(j));
        if (findThreads.isEmpty()) {
            return null;
        }
        return (Thread) findThreads.iterator().next();
    }

    public static Collection<Thread> findThreads(ThreadPredicate threadPredicate) {
        return findThreads(getSystemThreadGroup(), true, threadPredicate);
    }

    public static Collection<ThreadGroup> findThreadGroups(ThreadGroupPredicate threadGroupPredicate) {
        return findThreadGroups(getSystemThreadGroup(), true, threadGroupPredicate);
    }

    public static Collection<Thread> findThreads(ThreadGroup threadGroup, boolean z, ThreadPredicate threadPredicate) {
        Thread[] threadArr;
        int enumerate;
        if (threadGroup == null) {
            throw new IllegalArgumentException("The group must not be null");
        } else if (threadPredicate == null) {
            throw new IllegalArgumentException("The predicate must not be null");
        } else {
            int activeCount = threadGroup.activeCount();
            while (true) {
                threadArr = new Thread[(activeCount + (activeCount / 2) + 1)];
                enumerate = threadGroup.enumerate(threadArr, z);
                if (enumerate < threadArr.length) {
                    break;
                }
                activeCount = enumerate;
            }
            ArrayList arrayList = new ArrayList(enumerate);
            for (int i = 0; i < enumerate; i++) {
                if (threadPredicate.test(threadArr[i])) {
                    arrayList.add(threadArr[i]);
                }
            }
            return Collections.unmodifiableCollection(arrayList);
        }
    }

    public static Collection<ThreadGroup> findThreadGroups(ThreadGroup threadGroup, boolean z, ThreadGroupPredicate threadGroupPredicate) {
        ThreadGroup[] threadGroupArr;
        int enumerate;
        if (threadGroup == null) {
            throw new IllegalArgumentException("The group must not be null");
        } else if (threadGroupPredicate == null) {
            throw new IllegalArgumentException("The predicate must not be null");
        } else {
            int activeGroupCount = threadGroup.activeGroupCount();
            while (true) {
                threadGroupArr = new ThreadGroup[(activeGroupCount + (activeGroupCount / 2) + 1)];
                enumerate = threadGroup.enumerate(threadGroupArr, z);
                if (enumerate < threadGroupArr.length) {
                    break;
                }
                activeGroupCount = enumerate;
            }
            ArrayList arrayList = new ArrayList(enumerate);
            for (int i = 0; i < enumerate; i++) {
                if (threadGroupPredicate.test(threadGroupArr[i])) {
                    arrayList.add(threadGroupArr[i]);
                }
            }
            return Collections.unmodifiableCollection(arrayList);
        }
    }
}
