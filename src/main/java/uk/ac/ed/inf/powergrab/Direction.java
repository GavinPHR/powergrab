package uk.ac.ed.inf.powergrab;

// Enum class with all 16 compass directions
// For each direction, horizontal (longitudinal) and 
// vertical (latitudinal) change for a net change of 0.0003 unit
// are pre-computed 
public enum Direction {
    E   (3.0E-4,                 0.0),
    ENE (2.7716385975338597E-4,  1.1480502970952692E-4),
    NE  (2.1213203435596425E-4,  2.1213203435596422E-4),
    NNE (1.1480502970952694E-4,  2.7716385975338597E-4),
    N   (0,                      3.0E-4),
    NNW (-1.1480502970952691E-4, 2.7716385975338597E-4),
    NW  (-2.1213203435596422E-4, 2.1213203435596425E-4),
    WNW (-2.7716385975338597E-4, 1.1480502970952696E-4),
    W   (-3.0E-4,                0),
    WSW (-2.77163859753386E-4,   -1.148050297095269E-4),
    SW  (-2.1213203435596428E-4, -2.1213203435596422E-4),
    SSW (-1.1480502970952708E-4, -2.771638597533859E-4),
    S   (0,                      -3.0E-4),
    SSE (1.1480502970952699E-4,  -2.7716385975338597E-4),
    SE  (2.121320343559642E-4,   -2.1213203435596428E-4),
    ESE (2.771638597533859E-4,   -1.1480502970952711E-4);
    
    private double horizontal;
    private double vertical;
    
    private Direction(double h, double v) {
        horizontal = h;
        vertical = v;
    }
    
    double getHorizontal() {
        return horizontal;
    }
    
    double getVertical() {
        return vertical;
    }
}
