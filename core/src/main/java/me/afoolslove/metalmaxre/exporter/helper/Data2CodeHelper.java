package me.afoolslove.metalmaxre.exporter.helper;

import java.util.ArrayList;

public class Data2CodeHelper {

    public class Data {
        String name;
        int dimension;//0-not array, non-0 for array dimensions
        int type;//0-byte 1-short 2-int
        Object data;

        int index = 0;

        public void set(Object v) {
            data = v;
        }

        public void add(Object v) {
            ((Object[]) data)[index++] = v;
        }

        public String generateCppCode() {
            StringBuilder stringBuilder = new StringBuilder();

            return stringBuilder.toString();
        }

        public String generateHeadCode() {
            StringBuilder stringBuilder = new StringBuilder();

            return stringBuilder.toString();
        }
    }

    private ArrayList<Data> datas = new ArrayList<>();

    public Data2CodeHelper(String fileName) {

    }

    public Data newData(String name, int type, int... len) {
        Data data = new Data();
        data.name = name;
        data.dimension = len.length;
        data.type = type;

        if (len.length == 0) {
            switch (type) {
                case 0:
                    data.data = (byte) 0;
                    break;
                case 1:
                    data.data = (short) 0;
                    break;
                case 2:
                    data.data = (int) 0;
                    break;
            }
        } else {
            int realLen = len[0];
            for (int i = 1; i < len.length; i++) {
                realLen *= len[i];
            }
            switch (type) {
                case 0:
                    data.data = new byte[realLen];
                    break;
                case 1:
                    data.data = new short[realLen];
                    break;
                case 2:
                    data.data = new int[realLen];
                    break;
            }
        }
        datas.add(data);
        return data;
    }

}
