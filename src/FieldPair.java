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
            r += first.x + "-" + first.y;
        else r += "null";
        r += " -- ";
        if (second != null)
            r += second.x + "-" + second.y;
        else r += "null";
        return r;
    }
}