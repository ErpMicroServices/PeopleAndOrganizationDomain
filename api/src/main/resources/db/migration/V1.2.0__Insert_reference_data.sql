-- Insert reference data for type hierarchies and standard values
-- This migration must run after schema creation

-- Communication Event Purpose Types
INSERT INTO communication_event_purpose_type (description) VALUES ('Support Call') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Inquiry') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Customer Service Call') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Sales Follow Up') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Meeting') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Conference') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_purpose_type (description) VALUES ('Activity Request') ON CONFLICT (description) DO NOTHING;

-- Communication Event Types
INSERT INTO communication_event_type (description) VALUES ('Phone Communication') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_type (description) VALUES ('Fax Communication') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_type (description) VALUES ('Face-to-Face Communication') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_type (description) VALUES ('Letter Correspondence') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_type (description) VALUES ('Email Communication') ON CONFLICT (description) DO NOTHING;
INSERT INTO communication_event_type (description) VALUES ('Web Site Correspondence') ON CONFLICT (description) DO NOTHING;

-- Contact Mechanism Types
INSERT INTO contact_mechanism_type (description) VALUES ('Email Address') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('Facebook') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('IP Address') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('Postal Address') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('Telecommunications Number') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('Twitter') ON CONFLICT (description) DO NOTHING;
INSERT INTO contact_mechanism_type (description) VALUES ('Web Address') ON CONFLICT (description) DO NOTHING;

-- Geographic Boundary Types
INSERT INTO geographic_boundary_type (description) VALUES ('Country') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Postal Code') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Province') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Territory') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('State') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('County') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('City') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Sales Territory') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Service Territory') ON CONFLICT (description) DO NOTHING;
INSERT INTO geographic_boundary_type (description) VALUES ('Region') ON CONFLICT (description) DO NOTHING;

-- Insert United States as base country
INSERT INTO geographic_boundary (geo_code, name, abbreviation, geographic_boundary_type_id)
SELECT '', 'United States', 'US', gbt.id
FROM geographic_boundary_type gbt
WHERE gbt.description = 'Country'
ON CONFLICT DO NOTHING;

-- Party Relationship Status Types
INSERT INTO party_relationship_status_type (description) VALUES ('Leads') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_status_type (description) VALUES ('Prospects') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_status_type (description) VALUES ('Customers') ON CONFLICT (description) DO NOTHING;

-- Party Relationship Types
INSERT INTO party_relationship_type (description) VALUES ('Supplier Relationship') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Organization Contact Relationship') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Employment') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Customer Relationship') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Distribution Channel Relationship') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Partnership') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Organization Rollup') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Web Master Assignment') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Visitor ISP') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_relationship_type (description) VALUES ('Host Server Visitor') ON CONFLICT (description) DO NOTHING;

-- Party Role Types hierarchy
INSERT INTO party_role_type (description) VALUES ('Person Role') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_role_type (description, parent_id)
SELECT 'Employee', prt.id
FROM party_role_type prt
WHERE prt.description = 'Person Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Contractor', prt.id
FROM party_role_type prt
WHERE prt.description = 'Person Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Family Member', prt.id
FROM party_role_type prt
WHERE prt.description = 'Person Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Contact', prt.id
FROM party_role_type prt
WHERE prt.description = 'Person Role'
ON CONFLICT (description) DO NOTHING;

-- Organization Role Types
INSERT INTO party_role_type (description) VALUES ('Organization Role') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_role_type (description, parent_id)
SELECT 'Distribution Channel', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Agent', prt.id
FROM party_role_type prt
WHERE prt.description = 'Distribution Channel'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Distributor', prt.id
FROM party_role_type prt
WHERE prt.description = 'Distribution Channel'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Partner', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Competitor', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Household', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Regulatory Agency', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Supplier', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Association', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Organization Unit', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Parent Organization', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Unit'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Department', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Unit'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Subsidiary', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Unit'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Division', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Unit'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Internal Organization', prt.id
FROM party_role_type prt
WHERE prt.description = 'Organization Role'
ON CONFLICT (description) DO NOTHING;

-- Customer Role Types
INSERT INTO party_role_type (description) VALUES ('Customer') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_role_type (description, parent_id)
SELECT 'Bill to Customer', prt.id
FROM party_role_type prt
WHERE prt.description = 'Customer'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'Ship to Customer', prt.id
FROM party_role_type prt
WHERE prt.description = 'Customer'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description, parent_id)
SELECT 'End User Customer', prt.id
FROM party_role_type prt
WHERE prt.description = 'Customer'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_role_type (description) VALUES ('Prospect') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_role_type (description) VALUES ('Shareholder') ON CONFLICT (description) DO NOTHING;

-- Party Types
INSERT INTO party_type (description) VALUES ('Person') ON CONFLICT (description) DO NOTHING;
INSERT INTO party_type (description) VALUES ('Organization') ON CONFLICT (description) DO NOTHING;

INSERT INTO party_type (description, parent_id)
SELECT 'Legal Organization', pt.id
FROM party_type pt
WHERE pt.description = 'Organization'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_type (description, parent_id)
SELECT 'Informal Organization', pt.id
FROM party_type pt
WHERE pt.description = 'Organization'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_type (description, parent_id)
SELECT 'Corporation', pt.id
FROM party_type pt
WHERE pt.description = 'Legal Organization'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_type (description, parent_id)
SELECT 'Government Agency', pt.id
FROM party_type pt
WHERE pt.description = 'Legal Organization'
ON CONFLICT (description) DO NOTHING;

INSERT INTO party_type (description, parent_id)
SELECT 'Team', pt.id
FROM party_type pt
WHERE pt.description = 'Informal Organization'
ON CONFLICT (description) DO NOTHING;

-- Facility Types
INSERT INTO facility_type (description) VALUES ('Warehouse') ON CONFLICT (description) DO NOTHING;
INSERT INTO facility_type (description) VALUES ('Plant') ON CONFLICT (description) DO NOTHING;
INSERT INTO facility_type (description) VALUES ('Building') ON CONFLICT (description) DO NOTHING;
INSERT INTO facility_type (description) VALUES ('Floor') ON CONFLICT (description) DO NOTHING;
INSERT INTO facility_type (description) VALUES ('Office') ON CONFLICT (description) DO NOTHING;
INSERT INTO facility_type (description) VALUES ('Room') ON CONFLICT (description) DO NOTHING;
