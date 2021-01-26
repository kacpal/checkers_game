class FieldPair {
    Field first;
    Field second;

    FieldPair() {}

    FieldPair(Field a, Field b) {
        first = a;
        second = b;
    }

    @Override
    public String toString() {
        String r = "";
        if (first != null)
            r += (char)('a'+first.y) + "-" + first.x;
        else r += "null";
        r += " -- ";
        if (second != null)
            r += (char)('a'+second.y) + "-" + second.x;
        else r += "null";
        return r;
    }
}