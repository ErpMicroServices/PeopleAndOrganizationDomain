-- Rollback script for V1.3.0__Insert_us_states.sql
-- WARNING: This will remove all US states and territories data

DELETE FROM geographic_boundary_association
WHERE within_boundary IN (SELECT id FROM geographic_boundary WHERE name = 'United States')
  AND in_boundary IN (
    SELECT id FROM geographic_boundary
    WHERE geographic_boundary_type_id IN (
      SELECT id FROM geographic_boundary_type
      WHERE description IN ('State', 'Territory')
    )
  );

DELETE FROM geographic_boundary
WHERE geographic_boundary_type_id IN (
  SELECT id FROM geographic_boundary_type
  WHERE description IN ('State', 'Territory')
);
