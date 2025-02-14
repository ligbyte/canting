package com.stkj.cashier.base.utils;

import java.util.Arrays;

/* loaded from: alibaba-dingtalk-service-sdk-2.0.0.jar:com/taobao/api/internal/util/Base64.class */
public class Base64 {

    /* renamed from: CA */
    private static final char[] f214CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /* renamed from: IA */
    private static final int[] f215IA = new int[256];

    static {
        Arrays.fill(f215IA, -1);
        int iS = f214CA.length;
        for (int i = 0; i < iS; i++) {
            f215IA[f214CA[i]] = i;
        }
        f215IA[61] = 0;
    }

    public static final char[] encodeToChar(byte[] sArr, boolean lineSep) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new char[0];
        }
        int eLen = (sLen / 3) * 3;
        int cCnt = (((sLen - 1) / 3) + 1) << 2;
        int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76) << 1 : 0);
        char[] dArr = new char[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            int i = s;
            int s2 = s + 1;
            int s3 = s2 + 1;
            s = s3 + 1;
            int i2 = ((sArr[i] & 255) << 16) | ((sArr[s2] & 255) << 8) | (sArr[s3] & 255);
            int i3 = d;
            int d2 = d + 1;
            dArr[i3] = f214CA[(i2 >>> 18) & 63];
            int d3 = d2 + 1;
            dArr[d2] = f214CA[(i2 >>> 12) & 63];
            int d4 = d3 + 1;
            dArr[d3] = f214CA[(i2 >>> 6) & 63];
            d = d4 + 1;
            dArr[d4] = f214CA[i2 & 63];
            if (lineSep) {
                cc++;
                if (cc == 19 && d < dLen - 2) {
                    int d5 = d + 1;
                    dArr[d] = '\r';
                    d = d5 + 1;
                    dArr[d5] = '\n';
                    cc = 0;
                }
            }
        }
        int left = sLen - eLen;
        if (left > 0) {
            int i4 = ((sArr[eLen] & 255) << 10) | (left == 2 ? (sArr[sLen - 1] & 255) << 2 : 0);
            dArr[dLen - 4] = f214CA[i4 >> 12];
            dArr[dLen - 3] = f214CA[(i4 >>> 6) & 63];
            dArr[dLen - 2] = left == 2 ? f214CA[i4 & 63] : '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }

    public static final byte[] decode(char[] sArr) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            if (f215IA[sArr[i]] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i2 = sLen;
        while (i2 > 1) {
            i2--;
            if (f215IA[sArr[i2]] > 0) {
                break;
            } else if (sArr[i2] == '=') {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i3 = 0;
            int j = 0;
            while (j < 4) {
                int i4 = s;
                s++;
                int c = f215IA[sArr[i4]];
                if (c >= 0) {
                    i3 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i5 = d;
            d++;
            dArr[i5] = (byte) (i3 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i3 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i3;
                }
            }
        }
        return dArr;
    }

    public static final byte[] decodeFast(char[] sArr) {
        int i;
        int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && f215IA[sArr[sIx]] < 0) {
            sIx++;
        }
        while (eIx > 0 && f215IA[sArr[eIx]] < 0) {
            eIx--;
        }
        int pad = sArr[eIx] == '=' ? sArr[eIx - 1] == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (sArr[76] == '\r' ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int sIx4 = sIx3 + 1;
            sIx = sIx4 + 1;
            int i3 = (f215IA[sArr[i2]] << 18) | (f215IA[sArr[sIx2]] << 12) | (f215IA[sArr[sIx3]] << 6) | f215IA[sArr[sIx4]];
            int i4 = d;
            int d2 = d + 1;
            dArr[i4] = (byte) (i3 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i3 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i3;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i5 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i6 = sIx;
                sIx++;
                i5 |= f215IA[sArr[i6]] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i7 = d;
                d++;
                dArr[i7] = (byte) (i5 >> r);
                r -= 8;
            }
        }
        return dArr;
    }

    public static final byte[] encodeToByte(byte[] sArr, boolean lineSep) {
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int eLen = (sLen / 3) * 3;
        int cCnt = (((sLen - 1) / 3) + 1) << 2;
        int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76) << 1 : 0);
        byte[] dArr = new byte[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            int i = s;
            int s2 = s + 1;
            int s3 = s2 + 1;
            s = s3 + 1;
            int i2 = ((sArr[i] & 255) << 16) | ((sArr[s2] & 255) << 8) | (sArr[s3] & 255);
            int i3 = d;
            int d2 = d + 1;
            dArr[i3] = (byte) f214CA[(i2 >>> 18) & 63];
            int d3 = d2 + 1;
            dArr[d2] = (byte) f214CA[(i2 >>> 12) & 63];
            int d4 = d3 + 1;
            dArr[d3] = (byte) f214CA[(i2 >>> 6) & 63];
            d = d4 + 1;
            dArr[d4] = (byte) f214CA[i2 & 63];
            if (lineSep) {
                cc++;
                if (cc == 19 && d < dLen - 2) {
                    int d5 = d + 1;
                    dArr[d] = 13;
                    d = d5 + 1;
                    dArr[d5] = 10;
                    cc = 0;
                }
            }
        }
        int left = sLen - eLen;
        if (left > 0) {
            int i4 = ((sArr[eLen] & 255) << 10) | (left == 2 ? (sArr[sLen - 1] & 255) << 2 : 0);
            dArr[dLen - 4] = (byte) f214CA[i4 >> 12];
            dArr[dLen - 3] = (byte) f214CA[(i4 >>> 6) & 63];
            dArr[dLen - 2] = left == 2 ? (byte) f214CA[i4 & 63] : (byte) 61;
            dArr[dLen - 1] = 61;
        }
        return dArr;
    }

    public static final byte[] decode(byte[] sArr) {
        int sLen = sArr.length;
        int sepCnt = 0;
        for (byte b : sArr) {
            if (f215IA[b & 255] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i = sLen;
        while (i > 1) {
            i--;
            if (f215IA[sArr[i] & 255] > 0) {
                break;
            } else if (sArr[i] == 61) {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i2 = 0;
            int j = 0;
            while (j < 4) {
                int i3 = s;
                s++;
                int c = f215IA[sArr[i3] & 255];
                if (c >= 0) {
                    i2 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i4 = d;
            d++;
            dArr[i4] = (byte) (i2 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i2 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i2;
                }
            }
        }
        return dArr;
    }

    public static final byte[] decodeFast(byte[] sArr) {
        int i;
        int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && f215IA[sArr[sIx] & 255] < 0) {
            sIx++;
        }
        while (eIx > 0 && f215IA[sArr[eIx] & 255] < 0) {
            eIx--;
        }
        int pad = sArr[eIx] == 61 ? sArr[eIx - 1] == 61 ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (sArr[76] == 13 ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int sIx4 = sIx3 + 1;
            sIx = sIx4 + 1;
            int i3 = (f215IA[sArr[i2]] << 18) | (f215IA[sArr[sIx2]] << 12) | (f215IA[sArr[sIx3]] << 6) | f215IA[sArr[sIx4]];
            int i4 = d;
            int d2 = d + 1;
            dArr[i4] = (byte) (i3 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i3 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i3;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i5 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i6 = sIx;
                sIx++;
                i5 |= f215IA[sArr[i6]] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i7 = d;
                d++;
                dArr[i7] = (byte) (i5 >> r);
                r -= 8;
            }
        }
        return dArr;
    }

    public static final String encodeToString(byte[] sArr, boolean lineSep) {
        return new String(encodeToChar(sArr, lineSep));
    }

    public static final byte[] decode(String str) {
        int sLen = str != null ? str.length() : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            if (f215IA[str.charAt(i)] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int i2 = sLen;
        while (i2 > 1) {
            i2--;
            if (f215IA[str.charAt(i2)] > 0) {
                break;
            } else if (str.charAt(i2) == '=') {
                pad++;
            }
        }
        int len = (((sLen - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int i3 = 0;
            int j = 0;
            while (j < 4) {
                int i4 = s;
                s++;
                int c = f215IA[str.charAt(i4)];
                if (c >= 0) {
                    i3 |= c << (18 - (j * 6));
                } else {
                    j--;
                }
                j++;
            }
            int i5 = d;
            d++;
            dArr[i5] = (byte) (i3 >> 16);
            if (d < len) {
                d++;
                dArr[d] = (byte) (i3 >> 8);
                if (d < len) {
                    d++;
                    dArr[d] = (byte) i3;
                }
            }
        }
        return dArr;
    }

    public static final boolean isBase64Value(String str) {
        int sLen = str != null ? str.length() : 0;
        if (sLen == 0) {
            return false;
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; i++) {
            char currentChar = str.charAt(i);
            if (currentChar >= f215IA.length) {
                return false;
            }
            if (f215IA[currentChar] < 0) {
                sepCnt++;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return false;
        }
        return true;
    }

    public static final byte[] decodeFast(String s) {
        int i;
        int sLen = s.length();
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx = 0;
        int eIx = sLen - 1;
        while (sIx < eIx && f215IA[s.charAt(sIx) & 255] < 0) {
            sIx++;
        }
        while (eIx > 0 && f215IA[s.charAt(eIx) & 255] < 0) {
            eIx--;
        }
        int pad = s.charAt(eIx) == '=' ? s.charAt(eIx - 1) == '=' ? 2 : 1 : 0;
        int cCnt = (eIx - sIx) + 1;
        if (sLen > 76) {
            i = (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1;
        } else {
            i = 0;
        }
        int sepCnt = i;
        int len = (((cCnt - sepCnt) * 6) >> 3) - pad;
        byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        int eLen = (len / 3) * 3;
        while (d < eLen) {
            int i2 = sIx;
            int sIx2 = sIx + 1;
            int sIx3 = sIx2 + 1;
            int sIx4 = sIx3 + 1;
            sIx = sIx4 + 1;
            int i3 = (f215IA[s.charAt(i2)] << 18) | (f215IA[s.charAt(sIx2)] << 12) | (f215IA[s.charAt(sIx3)] << 6) | f215IA[s.charAt(sIx4)];
            int i4 = d;
            int d2 = d + 1;
            dArr[i4] = (byte) (i3 >> 16);
            int d3 = d2 + 1;
            dArr[d2] = (byte) (i3 >> 8);
            d = d3 + 1;
            dArr[d3] = (byte) i3;
            if (sepCnt > 0) {
                cc++;
                if (cc == 19) {
                    sIx += 2;
                    cc = 0;
                }
            }
        }
        if (d < len) {
            int i5 = 0;
            int j = 0;
            while (sIx <= eIx - pad) {
                int i6 = sIx;
                sIx++;
                i5 |= f215IA[s.charAt(i6)] << (18 - (j * 6));
                j++;
            }
            int r = 16;
            while (d < len) {
                int i7 = d;
                d++;
                dArr[i7] = (byte) (i5 >> r);
                r -= 8;
            }
        }
        return dArr;
    }
}