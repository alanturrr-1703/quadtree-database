
# QuadTree Geocoding System 🌍

This project implements a **quad-based geocoding system** in Java — allowing you to **encode geographic coordinates (latitude, longitude)** into a compact **quad code**, and **decode** them back into approximate coordinates.

It uses a **quadtree**-based spatial partitioning structure to recursively subdivide the Earth’s surface into four regions:

* **NW (1)**
* **NE (2)**
* **SW (3)**
* **SE (4)**

---

## 📦 Package

```
com.plusCode.quadtree
```

---

## 🚀 Features

* **Hierarchical spatial partitioning** using a quadtree structure
* **Encoding**: Convert latitude/longitude to a quad code string
* **Decoding**: Convert quad code back to approximate center coordinates
* **Customizable depth** for precision control
* **Simple and extensible** design for integration with GIS or location-based systems

---

## 🧠 Core Concepts

Each **Quadrant** represents a rectangular region on Earth, bounded by:

* `minLat`, `maxLat` (latitude range)
* `minLon`, `maxLon` (longitude range)

The root quadrant represents the entire Earth:

```
Latitude:  -90° to +90°
Longitude: -180° to +180°
```

Each subdivision splits a region into four child quadrants:

```
+-----------+-----------+
|     1     |     2     |
|    NW     |    NE     |
+-----------+-----------+
|     3     |     4     |
|    SW     |    SE     |
+-----------+-----------+
```

---

## ⚙️ Usage Example

### 1. Encoding

```java
Quadrant root = new Quadrant(null, -90, 90, -180, 180);
String code = root.encode(root, 6, 37.7749, -122.4194); // San Francisco
System.out.println("Encoded quad code: " + code);
```

### 2. Decoding

```java
double[] coords = Quadrant.decode(code);
System.out.println("Decoded center: Lat=" + coords[0] + ", Lon=" + coords[1]);
```

---

## 🧩 Class Overview

| Method                                                     | Description                                                        |
| ---------------------------------------------------------- | ------------------------------------------------------------------ |
| `subdivide()`                                              | Splits the current quadrant into four children (NW, NE, SW, SE).   |
| `encode(root, depth, lat, lon)`                            | Converts a coordinate into a quad code up to the given depth.      |
| `decode(code)`                                             | Converts a quad code back into approximate latitude and longitude. |
| `getMinLat()`, `getMaxLat()`, `getMinLon()`, `getMaxLon()` | Get bounds of the current quadrant.                                |
| `getMidLat()`, `getMidLon()`                               | Get the midpoint of the quadrant.                                  |

---

## 🧮 Example Output

```
Encoded quad code: 214231
Decoded center: Lat=37.78125, Lon=-122.40625
```

---

## 📈 Applications

* Spatial indexing and search
* Geohashing and location encoding
* Tile-based world rendering (e.g., game maps, Earth viewers)
* Efficient database sharding by geographic region

---

## 🧰 Requirements

* Java 8 or later
* No external dependencies

---

## 📜 License

This project is released under the **MIT License** — free to use, modify, and distribute.

