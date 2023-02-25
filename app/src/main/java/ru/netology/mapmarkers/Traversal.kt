package ru.netology.mapmarkers

import com.yandex.mapkit.map.*

class Traversal(val mainActivity: MainActivity) : MapObjectVisitor {
    override fun onPlacemarkVisited(mapObject: PlacemarkMapObject) {
        //mainActivity.test2(mapObject)
    }

    override fun onPolylineVisited(p0: PolylineMapObject) {
    }

    override fun onPolygonVisited(p0: PolygonMapObject) {
    }

    override fun onCircleVisited(p0: CircleMapObject) {
    }

    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
        println("Collection start")
        return true
    }

    override fun onCollectionVisitEnd(p0: MapObjectCollection) {
        println("Collection end")
    }

    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
        return true
    }

    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
    }
}