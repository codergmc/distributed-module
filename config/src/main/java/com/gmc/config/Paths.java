package com.gmc.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

public class Paths implements Iterable<Paths.Path> {

    private PriorityQueue<SameLevelPath> paths = new PriorityQueue<>();
    private List<SameLevelPath> fullPath = new ArrayList<>();
    private Iterator<Path> iterator = new MyIterator();

    public Paths(List<List<String>> paths) {
        assert paths != null;
        int i = 0;
        for (List<String> sameLevelPath : paths) {
            SameLevelPath sameLevelPath1 = new SameLevelPath(sameLevelPath, i);
            this.paths.add(sameLevelPath1);
            fullPath.add(sameLevelPath1);
            i++;
        }
    }

    public void pathProcess(Function<String, ConfigProperty> processFunction, ConfigProperty head) {
        for (Path path : this) {
            GlobalParams.INSTANCE.addParam(Config.CUR_SAME_LEVEL_PATH, path.getSameLevelPath());
            GlobalParams.INSTANCE.addParam(Config.CUR_PATH, path);
            HierarchyConfigProperty sameLevelPath;
            try {
                sameLevelPath = (HierarchyConfigProperty) GlobalParams.getHeadConfigProperty().get(path.getSameLevelPath().getRelativeIndex());
            } catch (Exception e) {
                sameLevelPath = new HierarchyConfigProperty(new ConfigProperties());
                head.add(path.getSameLevelPath().getRelativeIndex(), sameLevelPath);
            }
            GlobalParams.INSTANCE.addParam(Config.CUR_SAME_LEVEL_HEAD_CONFIG_PROPERTY, sameLevelPath);
            ConfigProperty configProperty = processFunction.apply(path.getPath());
            if (configProperty == null) {
                configProperty = new ConfigPropertyImpl();
            }

            sameLevelPath.addInner(path.getRelativeIndex(), configProperty);

            path.oneLoopDone();
            if (path.isSameLevelPathEnd()) {
                GlobalParams.getProcessors().processSameLevelPaths(GlobalParams.getCurrentSameLevelConfigProperty());
            }

        }
    }


    public Path get(int index, int innerIndex) {
        return fullPath.get(index).get(innerIndex);
    }

    public void addInHeadConfigProperty(SameLevelPath sameLevelPath) {
        SameLevelPath temp = null;
        PriorityQueue<SameLevelPath> newPaths = new PriorityQueue<>();
        while ((temp = paths.poll()) != null) {
            if (temp.getRelativeIndex() < sameLevelPath.getRelativeIndex()) {
                newPaths.add(temp);
            } else {
                temp.incrIndex();
                newPaths.add(temp);
            }
        }
        newPaths.add(sameLevelPath);
        fullPath.add(sameLevelPath.getRelativeIndex(), sameLevelPath);

    }


    public SameLevelPath get(int index) {
        return fullPath.get(index);
    }

    @Override
    public Iterator<Path> iterator() {
        return iterator;
    }

    class MyIterator implements Iterator<Path> {

        @Override
        public boolean hasNext() {
            SameLevelPath peek = paths.peek();

            if (peek == null) {
                return false;
            } else {
                return peek.iterator().hasNext();
            }
        }

        @Override
        public Path next() {
            SameLevelPath peek = paths.peek();
            Iterator<Path> iterator = peek.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            } else {
                paths.poll();
                return next();
            }


        }
    }


    class SameLevelPath implements Iterable<Path>, Comparable<SameLevelPath> {
        PriorityQueue<Path> paths = new PriorityQueue<>();
        int absoluteParentIndex;
        //用于连接多个ConfigProperty
        int relativeIndex;
        List<Path> fullPaths = new ArrayList<>();
        MyIterator myIterator = new MyIterator();

        public int getAbsoluteParentIndex() {
            return absoluteParentIndex;
        }

        public int getRelativeIndex() {
            return relativeIndex;
        }

        public void incrIndex() {
            this.absoluteParentIndex += 1;
        }

        public SameLevelPath(List<String> paths, int parentIndex) {
            for (int i = 0; i < paths.size(); i++) {
                Path path = new Path(paths.get(i), i, this);
                this.paths.add(path);
                this.fullPaths.add(path);
            }
            this.absoluteParentIndex = parentIndex;
            this.relativeIndex = parentIndex;
        }


        public Path get(int innerIndex) {
            return fullPaths.get(innerIndex);
        }

        public void add(int index, String path) {
            Path path1 = new Path(path, index, this);
            paths.add(path1);
            fullPaths.add(index, path1);
        }

        public void add(String path) {
            add(paths.size(), path);
        }

        @Override
        public Iterator<Path> iterator() {
            return myIterator;
        }

        @Override
        public int compareTo(SameLevelPath other) {
            return Integer.compare(this.absoluteParentIndex, other.getAbsoluteParentIndex());
        }

        class MyIterator implements Iterator<Path> {

            @Override
            public boolean hasNext() {
                Path peek = paths.peek();
                return peek != null;
            }

            @Override
            public Path next() {
                return paths.poll();
            }
        }

        ;
    }

    class Path implements Comparable<Path> {
        String path;
        int absoluteParentIndex;
        //用于连接多个ConfigProperty
        int relativeIndex;
        SameLevelPath sameLevelPath;

        public Path(String path, int parentIndex, SameLevelPath sameLevelPath) {
            this.path = path;
            this.absoluteParentIndex = parentIndex;
            this.relativeIndex = parentIndex;
            this.sameLevelPath = sameLevelPath;
        }

        public String getPath() {
            return path;
        }

        public void oneLoopDone() {
            relativeIndex = absoluteParentIndex;
            sameLevelPath.relativeIndex = sameLevelPath.absoluteParentIndex;
        }

        public void incrIndex() {
            this.absoluteParentIndex += 1;
        }

        public int getAbsoluteParentIndex() {
            return absoluteParentIndex;
        }

        public int getRelativeIndex() {
            return relativeIndex;
        }

        public SameLevelPath getSameLevelPath() {
            return sameLevelPath;
        }

        public Path setPath(String path) {
            this.path = path;
            return this;
        }


        public Path setSameLevelPath(SameLevelPath sameLevelPath) {
            this.sameLevelPath = sameLevelPath;
            return this;
        }

        public boolean isSameLevelPathFirst() {
            return relativeIndex == 0;
        }

        public boolean isSameLevelPathEnd() {
            assert relativeIndex == absoluteParentIndex;
            assert sameLevelPath.relativeIndex == sameLevelPath.absoluteParentIndex;
            return absoluteParentIndex == sameLevelPath.fullPaths.size() - 1;

        }

        @Override
        public int compareTo(Path other) {
            return Integer.compare(absoluteParentIndex, other.absoluteParentIndex);
        }
    }


}
