# Stoa

Stoa library provides [Structure of Arrays](https://en.wikipedia.org/wiki/AOS_and_SOA#Structure_of_arrays) in Java, compatible with the standard Collection interfaces.

## Usage

StoaBuilder provides static methods which return Supplier to construct a list, map or set.
For example StoaBuilder.list(BeanInfo info) yields a List<> compatible object that maps the Bean fields onto arrays. Similarly for map() and set().

```java
public class Point2D {
  private int x;
  private int y;
  private boolean insideVolume;
  // getter, setters and constructors here ...
};

public static void main(String args[]) throws IntrospectionException {

  Stoa<Point2D> points = StoaBuilder.<Point2D>list(Introspector.getBeanInfo(Point2D.class, Object.class)).get();
  points.add(new Point2D(1, 1, false));
  points.add(new Point2D(2, 3, true));
  points.add(new Point2D(5, 7, false));
  points.add(new Point2D(11, 13, true));
  System.out.println(Arrays.toString((int[]) points.data("x")));
  System.out.println(Arrays.toString((int[]) points.data("y")));
  System.out.println(Arrays.toString((long[]) points.data("insideVolume"))); // Note that booleans are stored inside a long array
}
```

## Building
$ mvn package

## License
GNU LGPL v3. Please see LICENSE file for details.
