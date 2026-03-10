package ispp.project.dondesiempre.utils.cloudinary;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoordinatesService {

    private final GeometryFactory geometryFactory;

    public Point createPoint(double lon, double lat) {
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}