/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru. See the AUTHORS file for more information.
 * 
 * This file is part of AmIciTy-Mi.
 * 
 * AmIciTy-Mi is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * AmIciTy-Mi is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with AmIciTy-Mi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package base.graphics;

/*
 import java.awt.Color;

 import javax.media.j3d.Appearance;
 import javax.media.j3d.ColoringAttributes;
 import javax.media.j3d.GeometryArray;
 import javax.media.j3d.IndexedGeometryArray;
 import javax.media.j3d.IndexedLineArray;
 import javax.media.j3d.IndexedLineStripArray;
 import javax.media.j3d.Shape3D;
 import javax.vecmath.Color3f;
 import javax.vecmath.Point3d;

 import base.Environment;

 public abstract class AbstractSurfaceViewer extends AbstractViewer3D {
 private final int n = 5;
 private int nx;
 private int ny;
 private IndexedLineStripArray wire;

 protected AbstractSurfaceViewer(Environment cm, Object data) {
 super(cm, data);
 setSize(300, 300);
 }

 protected AbstractSurfaceViewer(Environment cm) {
 this(cm, null);
 }

 protected abstract double getHeight(int x, int y);

 protected abstract boolean isHighlighted(int x, int y);

 private double p(double t, double a, double b, double c, double d) {
 return (2 * b + (c - a) * t + (2 * a - 5 * b + 4 * c - d) * t * t
 + (-a + 3 * b - 3 * c + d) * t * t * t) / 2;
 }

 private double val(int ix, int iy) {
 if (ix <= -1) {
 return 0;
 } else if (ix >= cm.sx) {
 return 0;
 } else if (iy <= -1) {
 return 0;
 } else if (iy >= cm.sy) {
 return 0;
 } else {
 return getHeight(ix, iy);
 }
 }

 private double app(double x, double y) {
 int ix = (int) Math.floor(x);
 int iy = (int) Math.floor(y);
 double tx = (x - ix);
 double ty = (y - iy);
 double a = p(tx, val(ix - 1, iy - 1), val(ix, iy - 1), val(ix + 1,
 iy - 1), val(ix + 2, iy - 1));
 double b = p(tx, val(ix - 1, iy), val(ix, iy), val(ix + 1, iy),
 val(ix + 2, iy));
 double c = p(tx, val(ix - 1, iy + 1), val(ix, iy + 1), val(ix + 1,
 iy + 1), val(ix + 2, iy + 1));
 double d = p(tx, val(ix - 1, iy + 2), val(ix, iy + 2), val(ix + 1,
 iy + 2), val(ix + 2, iy + 2));
 return p(ty, a, b, c, d);
 }

 @Override
 protected Shape3D createAxes() {
 double d = 0.01;
 Point3d[] vertices = {
 new Point3d(0.0, 0.0, 0.0), new Point3d(1.1, 0.0, 0.0), new Point3d(0.0, 1.1, 0.0), new Point3d(0.0, 0.0, 1.1),
 new Point3d(0.0, 0.1, 0.0), new Point3d(d, 0.1, 0.0), new Point3d(0.0, 0.1, d),
 new Point3d(0.0, 0.2, 0.0), new Point3d(d, 0.2, 0.0), new Point3d(0.0, 0.2, d),
 new Point3d(0.0, 0.3, 0.0), new Point3d(d, 0.3, 0.0), new Point3d(0.0, 0.3, d),
 new Point3d(0.0, 0.4, 0.0), new Point3d(d, 0.4, 0.0), new Point3d(0.0, 0.4, d),
 new Point3d(0.0, 0.5, 0.0), new Point3d(d, 0.5, 0.0), new Point3d(0.0, 0.5, d),
 new Point3d(0.0, 0.6, 0.0), new Point3d(d, 0.6, 0.0), new Point3d(0.0, 0.6, d),
 new Point3d(0.0, 0.7, 0.0), new Point3d(d, 0.7, 0.0), new Point3d(0.0, 0.7, d),
 new Point3d(0.0, 0.8, 0.0), new Point3d(d, 0.8, 0.0), new Point3d(0.0, 0.8, d),
 new Point3d(0.0, 0.9, 0.0), new Point3d(d, 0.9, 0.0), new Point3d(0.0, 0.9, d),
 new Point3d(0.0, 1.0, 0.0), new Point3d(d, 1.0, 0.0), new Point3d(0.0, 1.0, d),								
 };
 int[] indices = {
 0, 1, 0, 2, 0, 3,
 4, 5, 4, 6,
 7, 8, 7, 9,
 10, 11, 10, 12,
 13, 14, 13, 15,
 16, 17, 16, 18,
 19, 20, 19, 21,
 22, 23, 22, 24,
 25, 26, 25, 27,
 28, 29, 28, 30,
 31, 32, 32, 33
 };

 IndexedLineArray axes = new IndexedLineArray(vertices.length, GeometryArray.COORDINATES, indices.length);
 axes.setCoordinates(0, vertices);
 axes.setCoordinateIndices(0, indices);

 Shape3D shape = new Shape3D();
 shape.setGeometry(axes);
 Appearance look = new Appearance();
 look.setColoringAttributes(new ColoringAttributes(new Color3f(Color.black), 10));
 shape.setAppearance(look);

 return shape;
 }

 @Override
 protected Shape3D createShape() {
 nx = 1 + (cm.sx - 1) * n;
 ny = 1 + (cm.sy - 1) * n;

 int[] strip = new int[nx + ny];
 for (int i = 0; i < nx; i++) {
 strip[i] = ny;
 }
 for (int i = 0; i < ny; i++) {
 strip[nx + i] = nx;
 }

 wire = new IndexedLineStripArray(nx * ny, GeometryArray.COORDINATES | GeometryArray.COLOR_3, 2 * nx * ny, strip);
 wire.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
 wire.setCapability(IndexedGeometryArray.ALLOW_COLOR_INDEX_WRITE);

 update();

 int[] indices = new int[2 * nx * ny];
 for (int i = 0; i < nx; i++) {
 for (int j = 0; j < ny; j++) {
 indices[ny * i + j] = ny * i + j;
 }
 }
 for (int i = 0; i < ny; i++) {
 for (int j = 0; j < nx; j++) {
 indices[nx * ny + nx * i + j] = ny * j + i;
 }
 }
 wire.setCoordinateIndices(0, indices);

 Color3f[] colors = {new Color3f(0.5f, 0.5f, 0.5f), new Color3f(0.0f, 0.0f, 0.0f)};
 wire.setColors(0, colors);


 Shape3D shape = new Shape3D();
 shape.setGeometry(wire);

 return shape;
 }

 @Override
 public void update() {
 Point3d[] vertices = new Point3d[nx * ny];
 int[] colorIndices = new int[2 * nx * ny];
 for (int i = 0; i < nx; i++) {
 for (int j = 0; j < ny; j++) {
 double x = i / (double) n;
 double y = j / (double) n;
 double z = app(x, y);
 vertices[ny * i + j] = new Point3d(x / cm.sx, z, y / cm.sy);
 if (isHighlighted((i + n / 2) / n, (j + n / 2) / n)) {
 colorIndices[ny * i + j] = 1;
 colorIndices[nx * ny + nx * j + i] = 1;
 }
 }
 }
 wire.setCoordinates(0, vertices);
 wire.setColorIndices(0, colorIndices);
 }

 }
 */
