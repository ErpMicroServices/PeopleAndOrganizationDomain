-- Insert US states and territories data
-- This migration depends on reference data being loaded first

-- Insert US States
INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Alabama', 'AL', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, al.id
FROM geographic_boundary us, geographic_boundary al
WHERE us.name = 'United States' AND al.name = 'Alabama'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Alaska', 'AK', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ak.id
FROM geographic_boundary us, geographic_boundary ak
WHERE us.name = 'United States' AND ak.name = 'Alaska'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Arizona', 'AZ', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, az.id
FROM geographic_boundary us, geographic_boundary az
WHERE us.name = 'United States' AND az.name = 'Arizona'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Arkansas', 'AR', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ar.id
FROM geographic_boundary us, geographic_boundary ar
WHERE us.name = 'United States' AND ar.name = 'Arkansas'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'California', 'CA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ca.id
FROM geographic_boundary us, geographic_boundary ca
WHERE us.name = 'United States' AND ca.name = 'California'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Colorado', 'CO', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, co.id
FROM geographic_boundary us, geographic_boundary co
WHERE us.name = 'United States' AND co.name = 'Colorado'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Connecticut', 'CT', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ct.id
FROM geographic_boundary us, geographic_boundary ct
WHERE us.name = 'United States' AND ct.name = 'Connecticut'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Delaware', 'DE', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, de.id
FROM geographic_boundary us, geographic_boundary de
WHERE us.name = 'United States' AND de.name = 'Delaware'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Florida', 'FL', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, fl.id
FROM geographic_boundary us, geographic_boundary fl
WHERE us.name = 'United States' AND fl.name = 'Florida'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Georgia', 'GA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ga.id
FROM geographic_boundary us, geographic_boundary ga
WHERE us.name = 'United States' AND ga.name = 'Georgia'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Hawaii', 'HI', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, hi.id
FROM geographic_boundary us, geographic_boundary hi
WHERE us.name = 'United States' AND hi.name = 'Hawaii'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Idaho', 'ID', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, id_state.id
FROM geographic_boundary us, geographic_boundary id_state
WHERE us.name = 'United States' AND id_state.name = 'Idaho'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Illinois', 'IL', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, il.id
FROM geographic_boundary us, geographic_boundary il
WHERE us.name = 'United States' AND il.name = 'Illinois'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Indiana', 'IN', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, in_state.id
FROM geographic_boundary us, geographic_boundary in_state
WHERE us.name = 'United States' AND in_state.name = 'Indiana'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Iowa', 'IA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ia.id
FROM geographic_boundary us, geographic_boundary ia
WHERE us.name = 'United States' AND ia.name = 'Iowa'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Kansas', 'KS', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ks.id
FROM geographic_boundary us, geographic_boundary ks
WHERE us.name = 'United States' AND ks.name = 'Kansas'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Kentucky', 'KY', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ky.id
FROM geographic_boundary us, geographic_boundary ky
WHERE us.name = 'United States' AND ky.name = 'Kentucky'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Louisiana', 'LA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, la.id
FROM geographic_boundary us, geographic_boundary la
WHERE us.name = 'United States' AND la.name = 'Louisiana'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Maine', 'ME', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, me.id
FROM geographic_boundary us, geographic_boundary me
WHERE us.name = 'United States' AND me.name = 'Maine'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Maryland', 'MD', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, md.id
FROM geographic_boundary us, geographic_boundary md
WHERE us.name = 'United States' AND md.name = 'Maryland'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Massachusetts', 'MA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ma.id
FROM geographic_boundary us, geographic_boundary ma
WHERE us.name = 'United States' AND ma.name = 'Massachusetts'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Michigan', 'MI', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, mi.id
FROM geographic_boundary us, geographic_boundary mi
WHERE us.name = 'United States' AND mi.name = 'Michigan'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Minnesota', 'MN', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, mn.id
FROM geographic_boundary us, geographic_boundary mn
WHERE us.name = 'United States' AND mn.name = 'Minnesota'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Mississippi', 'MS', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ms.id
FROM geographic_boundary us, geographic_boundary ms
WHERE us.name = 'United States' AND ms.name = 'Mississippi'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Missouri', 'MO', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, mo.id
FROM geographic_boundary us, geographic_boundary mo
WHERE us.name = 'United States' AND mo.name = 'Missouri'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Montana', 'MT', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, mt.id
FROM geographic_boundary us, geographic_boundary mt
WHERE us.name = 'United States' AND mt.name = 'Montana'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Nebraska', 'NE', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ne.id
FROM geographic_boundary us, geographic_boundary ne
WHERE us.name = 'United States' AND ne.name = 'Nebraska'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Nevada', 'NV', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nv.id
FROM geographic_boundary us, geographic_boundary nv
WHERE us.name = 'United States' AND nv.name = 'Nevada'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'New Hampshire', 'NH', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nh.id
FROM geographic_boundary us, geographic_boundary nh
WHERE us.name = 'United States' AND nh.name = 'New Hampshire'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'New Jersey', 'NJ', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nj.id
FROM geographic_boundary us, geographic_boundary nj
WHERE us.name = 'United States' AND nj.name = 'New Jersey'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'New Mexico', 'NM', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nm.id
FROM geographic_boundary us, geographic_boundary nm
WHERE us.name = 'United States' AND nm.name = 'New Mexico'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'New York', 'NY', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ny.id
FROM geographic_boundary us, geographic_boundary ny
WHERE us.name = 'United States' AND ny.name = 'New York'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'North Carolina', 'NC', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nc.id
FROM geographic_boundary us, geographic_boundary nc
WHERE us.name = 'United States' AND nc.name = 'North Carolina'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'North Dakota', 'ND', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, nd.id
FROM geographic_boundary us, geographic_boundary nd
WHERE us.name = 'United States' AND nd.name = 'North Dakota'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Ohio', 'OH', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, oh.id
FROM geographic_boundary us, geographic_boundary oh
WHERE us.name = 'United States' AND oh.name = 'Ohio'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Oklahoma', 'OK', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ok.id
FROM geographic_boundary us, geographic_boundary ok
WHERE us.name = 'United States' AND ok.name = 'Oklahoma'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Oregon', 'OR', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, or_state.id
FROM geographic_boundary us, geographic_boundary or_state
WHERE us.name = 'United States' AND or_state.name = 'Oregon'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Pennsylvania', 'PA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, pa.id
FROM geographic_boundary us, geographic_boundary pa
WHERE us.name = 'United States' AND pa.name = 'Pennsylvania'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Rhode Island', 'RI', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ri.id
FROM geographic_boundary us, geographic_boundary ri
WHERE us.name = 'United States' AND ri.name = 'Rhode Island'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'South Carolina', 'SC', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, sc.id
FROM geographic_boundary us, geographic_boundary sc
WHERE us.name = 'United States' AND sc.name = 'South Carolina'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'South Dakota', 'SD', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, sd.id
FROM geographic_boundary us, geographic_boundary sd
WHERE us.name = 'United States' AND sd.name = 'South Dakota'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Tennessee', 'TN', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, tn.id
FROM geographic_boundary us, geographic_boundary tn
WHERE us.name = 'United States' AND tn.name = 'Tennessee'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Texas', 'TX', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, tx.id
FROM geographic_boundary us, geographic_boundary tx
WHERE us.name = 'United States' AND tx.name = 'Texas'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Utah', 'UT', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, ut.id
FROM geographic_boundary us, geographic_boundary ut
WHERE us.name = 'United States' AND ut.name = 'Utah'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Vermont', 'VT', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, vt.id
FROM geographic_boundary us, geographic_boundary vt
WHERE us.name = 'United States' AND vt.name = 'Vermont'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Virginia', 'VA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, va.id
FROM geographic_boundary us, geographic_boundary va
WHERE us.name = 'United States' AND va.name = 'Virginia'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Washington', 'WA', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, wa.id
FROM geographic_boundary us, geographic_boundary wa
WHERE us.name = 'United States' AND wa.name = 'Washington'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'West Virginia', 'WV', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, wv.id
FROM geographic_boundary us, geographic_boundary wv
WHERE us.name = 'United States' AND wv.name = 'West Virginia'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Wisconsin', 'WI', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, wi.id
FROM geographic_boundary us, geographic_boundary wi
WHERE us.name = 'United States' AND wi.name = 'Wisconsin'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Wyoming', 'WY', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'State'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, wy.id
FROM geographic_boundary us, geographic_boundary wy
WHERE us.name = 'United States' AND wy.name = 'Wyoming'
ON CONFLICT DO NOTHING;

-- Insert US Territories
INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'American Samoa', 'AS', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, as_territory.id
FROM geographic_boundary us, geographic_boundary as_territory
WHERE us.name = 'United States' AND as_territory.name = 'American Samoa'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Federated States of Micronesia', 'FM', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, fm.id
FROM geographic_boundary us, geographic_boundary fm
WHERE us.name = 'United States' AND fm.name = 'Federated States of Micronesia'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Guam', 'GU', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, gu.id
FROM geographic_boundary us, geographic_boundary gu
WHERE us.name = 'United States' AND gu.name = 'Guam'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Palau', 'PW', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, pw.id
FROM geographic_boundary us, geographic_boundary pw
WHERE us.name = 'United States' AND pw.name = 'Palau'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Puerto Rico', 'PR', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, pr.id
FROM geographic_boundary us, geographic_boundary pr
WHERE us.name = 'United States' AND pr.name = 'Puerto Rico'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'Virgin Islands', 'VI', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Territory'
ON CONFLICT DO NOTHING;

INSERT INTO geographic_boundary_association (within_boundary, in_boundary)
SELECT us.id, vi.id
FROM geographic_boundary us, geographic_boundary vi
WHERE us.name = 'United States' AND vi.name = 'Virgin Islands'
ON CONFLICT DO NOTHING;
