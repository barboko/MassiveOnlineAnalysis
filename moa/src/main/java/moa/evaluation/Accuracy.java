/*
 *    Accuracy.java
 *    Copyright (C) 2010 RWTH Aachen University, Germany
 *    @author Jansen (moa@cs.rwth-aachen.de)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package moa.evaluation;

import moa.cluster.Clustering;
import moa.gui.visualization.DataPoint;

import java.util.ArrayList;

public class Accuracy extends MeasureCollection implements ClassificationMeasureCollection {

    //private boolean debug = false;

    public Accuracy() {
        super();
    }

    @Override
    public String[] getNames() {
        return new String[]{"Accuracy", "Kappa", "Kappa Temp", "Ram-Hours", "Time", "Memory"};
    }

    @Override
    protected boolean[] getDefaultEnabled() {
        return new boolean[]{true, true, true, true, true, true};
    }

    public void evaluateClustering(Clustering clustering, Clustering trueClsutering, ArrayList<DataPoint> points) {

    }

}
