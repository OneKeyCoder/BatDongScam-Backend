# Database Documentation

This document provides a comprehensive overview of the database structure for the Real Estate System (RES) Backend. The system uses a hybrid database approach:
- **PostgreSQL** for relational entities
- **MongoDB** for analytics, logs, and dynamic data

---

## Table of Contents
1. [PostgreSQL Entities](#postgresql-entities)
   - [User Management](#user-management)
   - [Property Management](#property-management)
   - [Location Management](#location-management)
   - [Contract & Payment](#contract--payment)
   - [Appointment & Review](#appointment--review)
   - [Document Management](#document-management)
   - [Notification & Violation](#notification--violation)
2. [MongoDB Schemas](#mongodb-schemas)
   - [Customer Analytics](#customer-analytics)
   - [Ranking & Performance](#ranking--performance)
   - [Reports](#reports)
   - [Search Logs](#search-logs)

---

## PostgreSQL Entities

### User Management

#### 1. Users Table (`users`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| user_id | UUID | PK, NOT NULL, AUTO | Unique identifier for user |
| role | ENUM | NOT NULL | User role: ADMIN, SALESAGENT, GUEST, PROPERTY_OWNER, CUSTOMER (see Constants.RoleEnum) |
| email | VARCHAR | NOT NULL, UNIQUE | User's email address |
| phone_number | VARCHAR | NOT NULL, UNIQUE | User's phone number |
| address | TEXT | NOT NULL | User's address |
| password | VARCHAR | NOT NULL | Encrypted password |
| first_name | VARCHAR | NOT NULL | User's first name |
| last_name | VARCHAR | NOT NULL | User's last name |
| avatar_url | VARCHAR | NULL | URL to user's avatar image |
| status | ENUM | NOT NULL | Profile status: ACTIVE, SUSPENDED, PENDING_APPROVAL, REJECTED (see Constants.StatusProfileEnum) |
| last_login_at | TIMESTAMP | NULL | Last login timestamp |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-One with Customer, SaleAgent, PropertyOwner
- One-to-Many with Notifications, ViolationReports

---

#### 2. Customers Table (`customers`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| customer_id | UUID | PK, FK(users.user_id), NOT NULL | Customer identifier (same as user_id) |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-One with User (shared primary key)
- One-to-Many with Appointments, Contracts

---

#### 3. Sale Agents Table (`sale_agents`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| sale_agent_id | UUID | PK, FK(users.user_id), NOT NULL | Sale agent identifier (same as user_id) |
| employee_code | VARCHAR | NOT NULL, UNIQUE | Unique employee code |
| max_properties | INT | NOT NULL | Maximum properties an agent can handle |
| hired_date | TIMESTAMP | NOT NULL | Date when agent was hired |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-One with User (shared primary key)
- One-to-Many with Properties (assigned), Appointments, Contracts

---

#### 4. Property Owners Table (`property_owners`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| property_owner_id | UUID | PK, FK(users.user_id), NOT NULL | Property owner identifier (same as user_id) |
| identification_number | VARCHAR | NOT NULL, UNIQUE | Owner's identification/tax number |
| for_rent | INT | NOT NULL | Number of properties available for rent |
| for_sale | INT | NOT NULL | Number of properties available for sale |
| renting | INT | NOT NULL | Number of properties currently rented |
| sold | INT | NOT NULL | Number of properties sold |
| approved_at | TIMESTAMP | NULL | Timestamp when owner was approved |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-One with User (shared primary key)
- One-to-Many with Properties

---

### Property Management

#### 5. Properties Table (`properties`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| property_id | UUID | PK, NOT NULL, AUTO | Unique identifier for property |
| owner_id | UUID | FK(property_owners.property_owner_id), NOT NULL | Reference to property owner |
| assigned_agent_id | UUID | FK(sale_agents.sale_agent_id), NULL | Reference to assigned sale agent |
| service_fee_amount | DECIMAL(15,2) | NOT NULL | Service fee for listing |
| property_type_id | UUID | FK(property_types.property_type_id), NOT NULL | Reference to property type |
| ward_id | UUID | FK(wards.ward_id), NOT NULL | Reference to ward location |
| title | VARCHAR(200) | NOT NULL | Property listing title |
| description | TEXT | NOT NULL | Detailed property description |
| transaction_type | ENUM | NOT NULL | Type: SALE, RENTAL, INVESTMENT (see Constants.TransactionTypeEnum) |
| full_address | VARCHAR | NULL | Complete address string |
| area | DECIMAL(10,2) | NOT NULL | Property area in square meters |
| rooms | INT | NULL | Total number of rooms |
| bathrooms | INT | NULL | Number of bathrooms |
| floors | INT | NULL | Number of floors |
| bedrooms | INT | NULL | Number of bedrooms |
| house_orientation | ENUM | NULL | House facing direction (see Constants.OrientationEnum) |
| balcony_orientation | ENUM | NULL | Balcony facing direction (see Constants.OrientationEnum) |
| year_built | INT | NULL | Year the property was built |
| price_amount | DECIMAL(15,2) | NOT NULL | Listed price |
| price_per_square_meter | DECIMAL(15,2) | NULL | Price per square meter |
| commission_rate | DECIMAL(5,4) | NOT NULL | Agent commission rate |
| amenities | TEXT | NULL | Property amenities description |
| status | ENUM | NULL | Property status: PENDING, REJECTED, APPROVED, PAID, SOLD, RENTED, AVAILABLE, UNAVAILABLE, DELETED (see Constants.PropertyStatusEnum) |
| view_count | INT | NULL | Number of times property was viewed |
| approved_at | TIMESTAMP | NULL | Timestamp when property was approved |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with PropertyOwner, SaleAgent, PropertyType, Ward
- One-to-Many with Media, Appointments, Contracts, ViolationReports, IdentificationDocuments

---

#### 6. Property Types Table (`property_types`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| property_type_id | UUID | PK, NOT NULL, AUTO | Unique identifier for property type |
| type_name | VARCHAR(50) | NOT NULL, UNIQUE | Name of property type (e.g., Villa, Apartment) |
| avatar_url | VARCHAR | NULL | URL to property type image |
| description | TEXT | NULL | Description of property type |
| is_active | BOOLEAN | NULL | Whether this type is active |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-Many with Properties

---

#### 7. Media Table (`media`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| media_id | UUID | PK, NOT NULL, AUTO | Unique identifier for media |
| property_id | UUID | FK(properties.property_id), NOT NULL | Reference to property |
| media_type | ENUM | NOT NULL | Type: IMAGE, VIDEO, DOCUMENT (see Constants.MediaTypeEnum) |
| file_name | VARCHAR | NOT NULL | Name of the media file |
| file_path | VARCHAR(500) | NOT NULL | Path to stored media file |
| mime_type | VARCHAR(100) | NOT NULL | MIME type of the file |
| document_type | VARCHAR | NULL | Type of document if applicable |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with Property

---

### Location Management

#### 8. Cities Table (`cities`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| city_id | UUID | PK, NOT NULL, AUTO | Unique identifier for city |
| city_name | VARCHAR | NULL | Name of the city |
| description | VARCHAR | NULL | City description |
| img_url | VARCHAR | NULL | URL to city image |
| total_area | DECIMAL(15,2) | NULL | Total area in square kilometers |
| avg_land_price | DECIMAL(15,2) | NULL | Average land price in the city |
| population | INT | NULL | City population |
| is_active | BOOLEAN | NULL | Whether this city is active |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-Many with Districts

---

#### 9. Districts Table (`districts`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| district_id | UUID | PK, NOT NULL, AUTO | Unique identifier for district |
| city_id | UUID | FK(cities.city_id), NOT NULL | Reference to parent city |
| district_name | VARCHAR | NULL | Name of the district |
| img_url | VARCHAR | NULL | URL to district image |
| description | VARCHAR | NULL | District description |
| total_area | DECIMAL(15,2) | NULL | Total area in square kilometers |
| avg_land_price | DECIMAL(15,2) | NULL | Average land price in the district |
| population | INT | NULL | District population |
| is_active | BOOLEAN | NULL | Whether this district is active |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with City
- One-to-Many with Wards

---

#### 10. Wards Table (`wards`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| ward_id | UUID | PK, NOT NULL, AUTO | Unique identifier for ward |
| district_id | UUID | FK(districts.district_id), NOT NULL | Reference to parent district |
| ward_name | VARCHAR | NOT NULL | Name of the ward |
| img_url | VARCHAR | NULL | URL to ward image |
| description | VARCHAR | NOT NULL | Ward description |
| total_area | DECIMAL(15,2) | NOT NULL | Total area in square kilometers |
| avg_land_price | DECIMAL(15,2) | NULL | Average land price in the ward |
| population | INT | NOT NULL | Ward population |
| is_active | BOOLEAN | NULL | Whether this ward is active |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with District
- One-to-Many with Properties

---

### Contract & Payment

#### 11. Contract Table (`contract`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| contract_id | UUID | PK, NOT NULL, AUTO | Unique identifier for contract |
| property_id | UUID | FK(properties.property_id), NOT NULL | Reference to property |
| customer_id | UUID | FK(customers.customer_id), NOT NULL | Reference to customer |
| agent_id | UUID | FK(sale_agents.sale_agent_id), NOT NULL | Reference to sale agent |
| contract_type | ENUM | NOT NULL | Type: PURCHASE, RENTAL, INVESTMENT (see Constants.ContractTypeEnum) |
| contract_number | VARCHAR(50) | NOT NULL, UNIQUE | Unique contract number |
| start_date | DATE | NOT NULL | Contract start date |
| end_date | DATE | NOT NULL | Contract end date |
| special_terms | TEXT | NOT NULL | Special terms and conditions |
| status | ENUM | NOT NULL | Status: DRAFT, PENDING_SIGNING, ACTIVE, COMPLETED, CANCELLED (see Constants.ContractStatusEnum) |
| cancellation_reason | TEXT | NOT NULL | Reason for cancellation if applicable |
| cancellation_penalty | DECIMAL(15,2) | NOT NULL | Penalty for cancellation |
| contract_payment_type | ENUM | NOT NULL | Payment type: MORTGAGE, MONTHLY_RENT, PAID_IN_FULL (see Constants.ContractPaymentTypeEnum) |
| total_contract_amount | DECIMAL(15,2) | NOT NULL | Total contract value |
| deposit_amount | DECIMAL(15,2) | NOT NULL | Deposit amount paid |
| remaining_amount | DECIMAL(15,2) | NOT NULL | Remaining amount to be paid |
| advance_payment_amount | DECIMAL(15,2) | NOT NULL | Advance payment amount |
| installment_amount | INT | NOT NULL | Number of installments |
| progress_milestone | DECIMAL(15,2) | NOT NULL | Progress milestone percentage |
| final_payment_amount | DECIMAL(15,2) | NOT NULL | Final payment amount |
| late_payment_penalty_rate | DECIMAL(5,4) | NOT NULL | Late payment penalty rate |
| special_conditions | TEXT | NOT NULL | Special conditions |
| signed_at | TIMESTAMP | NOT NULL | Contract signing timestamp |
| completed_at | TIMESTAMP | NOT NULL | Contract completion timestamp |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with Property, Customer, SaleAgent
- One-to-Many with Payments
- One-to-One with Review

---

#### 12. Payments Table (`payments`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| payment_id | UUID | PK, NOT NULL, AUTO | Unique identifier for payment |
| contract_id | UUID | FK(contract.contract_id), NULL | Reference to contract |
| payment_type | ENUM | NOT NULL | Type: DEPOSIT, ADVANCE, INSTALLMENT, FULL_PAY, MONTHLY, PENALTY, REFUND, MONEY_SALE, MONEY_RENTAL, SALARY (see Constants.PaymentTypeEnum) |
| amount | DECIMAL(15,2) | NOT NULL | Payment amount |
| due_date | DATE | NOT NULL | Payment due date |
| paid_date | DATE | NULL | Actual payment date |
| installment_number | INT | NULL | Installment number if applicable |
| payment_method | VARCHAR | NULL | Payment method used |
| transaction_reference | VARCHAR(100) | NULL | Transaction reference number |
| status | VARCHAR | NULL | Payment status |
| overdue_days | INT | NULL | Number of overdue days |
| penalty_amount | DECIMAL(15,2) | NULL | Penalty amount for late payment |
| notes | TEXT | NULL | Additional notes |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with Contract

---

### Appointment & Review

#### 13. Appointment Table (`appointment`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| appointment_id | UUID | PK, NOT NULL, AUTO | Unique identifier for appointment |
| property_id | UUID | FK(properties.property_id), NOT NULL | Reference to property |
| customer_id | UUID | FK(customers.customer_id), NOT NULL | Reference to customer |
| agent_id | UUID | FK(sale_agents.sale_agent_id), NOT NULL | Reference to sale agent |
| requested_date | TIMESTAMP | NOT NULL | Requested appointment date/time |
| confirmed_date | TIMESTAMP | NULL | Confirmed appointment date/time |
| status | VARCHAR | NULL | Appointment status |
| customer_requirements | TEXT | NULL | Customer's requirements or notes |
| agent_notes | TEXT | NULL | Agent's notes |
| viewing_outcome | TEXT | NULL | Outcome of the viewing |
| customer_interest_level | VARCHAR | NULL | Customer's interest level |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with Property, Customer, SaleAgent
- One-to-One with Review

---

#### 14. Reviews Table (`reviews`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| review_id | UUID | PK, NOT NULL, AUTO | Unique identifier for review |
| appointment_id | UUID | FK(appointment.appointment_id), NULL | Reference to appointment |
| contract_id | UUID | FK(contract.contract_id), NULL | Reference to contract |
| rating | SMALLINT | NOT NULL | Rating value (typically 1-5) |
| comment | TEXT | NULL | Review comment |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-One with Appointment or Contract

---

### Document Management

#### 15. Document Types Table (`document_types`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| document_type_id | UUID | PK, NOT NULL, AUTO | Unique identifier for document type |
| name | VARCHAR(100) | NULL | Document type name |
| description | VARCHAR | NULL | Document type description |
| is_compulsory | BOOLEAN | NULL | Whether this document is mandatory |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- One-to-Many with IdentificationDocuments

---

#### 16. Identification Documents Table (`identification_documents`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| document_id | UUID | PK, NOT NULL, AUTO | Unique identifier for document |
| document_type_id | UUID | FK(document_types.document_type_id), NOT NULL | Reference to document type |
| property_id | UUID | FK(properties.property_id), NULL | Reference to property |
| document_number | VARCHAR(20) | NOT NULL | Document number |
| document_name | VARCHAR | NOT NULL | Document name |
| file_path | VARCHAR(500) | NOT NULL | Path to stored document |
| issue_date | DATE | NULL | Document issue date |
| expiry_date | DATE | NULL | Document expiry date |
| issuing_authority | VARCHAR(100) | NULL | Authority that issued the document |
| verification_status | ENUM | NULL | Status: PENDING, VERIFIED, REJECTED (see Constants.VerificationStatusEnum) |
| verified_at | TIMESTAMP | NULL | Verification timestamp |
| rejection_reason | TEXT | NULL | Reason for rejection if applicable |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with DocumentType, Property

---

### Notification & Violation

#### 17. Notifications Table (`notifications`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| notification_id | UUID | PK, NOT NULL, AUTO | Unique identifier for notification |
| recipient_id | UUID | FK(users.user_id), NOT NULL | Reference to recipient user |
| type | ENUM | NULL | Type: APPOINTMENT_REMINDER, CONTRACT_UPDATE, PAYMENT_DUE, VIOLATION_WARNING, SYSTEM_ALERT (see Constants.NotificationTypeEnum) |
| title | VARCHAR(200) | NOT NULL | Notification title |
| message | TEXT | NOT NULL | Notification message |
| related_entity_type | ENUM | NULL | Related entity: PROPERTY, CONTRACT, PAYMENT, APPOINTMENT, USER (see Constants.RelatedEntityTypeEnum) |
| related_entity_id | VARCHAR(100) | NULL | ID of related entity |
| delivery_status | ENUM | NULL | Status: PENDING, SENT, READ, FAILED (see Constants.NotificationStatusEnum) |
| is_read | BOOLEAN | NULL | Whether notification was read |
| img_url | VARCHAR | NOT NULL | Image URL for notification |
| read_at | TIMESTAMP | NULL | Timestamp when notification was read |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with User (recipient)

---

#### 18. Violation Reports Table (`violation_reports`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| violation_id | UUID | PK, NOT NULL, AUTO | Unique identifier for violation |
| user_id | UUID | FK(users.user_id), NULL | Reference to user who violated |
| property_id | UUID | FK(properties.property_id), NULL | Reference to property involved |
| violation_type | VARCHAR | NOT NULL | Type of violation |
| description | TEXT | NOT NULL | Violation description |
| severity | VARCHAR | NULL | Severity level |
| status | VARCHAR | NULL | Current status of violation |
| resolution_notes | TEXT | NULL | Notes on resolution |
| resolved_at | TIMESTAMP | NULL | Resolution timestamp |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Relationships:**
- Many-to-One with User, Property

---

## MongoDB Schemas

### Customer Analytics

#### 19. Customer Favorite Property (`customer_favorite_properties`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| ref_id | UUID | NOT NULL | Reference to property |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track properties favorited by customers for analytics and recommendations.

---

#### 20. Customer Preferred City (`customer_preferred_cities`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| ref_id | UUID | NOT NULL | Reference to city |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track customer preferences for cities to improve search and recommendations.

---

#### 21. Customer Preferred District (`customer_preferred_districts`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| ref_id | UUID | NOT NULL | Reference to district |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track customer preferences for districts to improve search and recommendations.

---

#### 22. Customer Preferred Ward (`customer_preferred_wards`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| ref_id | UUID | NOT NULL | Reference to ward |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track customer preferences for wards to improve search and recommendations.

---

#### 23. Customer Preferred Property Type (`customer_preferred_property_types`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| ref_id | UUID | NOT NULL | Reference to property type |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track customer preferences for property types to improve search and recommendations.

---

### Ranking & Performance

#### 24. Individual Sales Agent Performance Month (`individual_sales_agent_performance_month`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| agent_id | UUID | NOT NULL | Reference to sale agent |
| month | INT | NOT NULL | Month (1-12) |
| year | INT | NOT NULL | Year |
| performance_point | INT | NOT NULL | Performance points earned |
| performance_tier | ENUM | NOT NULL | Tier: BRONZE, SILVER, GOLD, PLATINUM (see Constants.PerformanceTierEnum) |
| ranking_position | INT | NOT NULL | Ranking position among agents |
| handling_properties | INT | NOT NULL | Number of properties currently handling |
| month_properties_assigned | INT | NOT NULL | Properties assigned in this month |
| month_appointment_completed | INT | NOT NULL | Appointments completed in this month |
| month_contracts | INT | NOT NULL | Contracts signed in this month |
| month_rates | INT | NOT NULL | Number of ratings received in this month |
| avg_rating | DECIMAL | NOT NULL | Average rating |
| month_customer_satisfaction_avg | DECIMAL | NOT NULL | Average customer satisfaction for the month |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track monthly performance metrics for sales agents.

---

#### 25. Individual Sales Agent Performance Career (`individual_sales_agent_performance_career`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| agent_id | UUID | NOT NULL | Reference to sale agent |
| performance_point | INT | NOT NULL | Total career performance points |
| career_ranking | INT | NOT NULL | Career ranking position |
| properties_assigned | INT | NOT NULL | Total properties assigned in career |
| appointment_completed | INT | NOT NULL | Total appointments completed in career |
| total_contracts | INT | NOT NULL | Total contracts signed in career |
| customer_satisfaction_avg | DECIMAL | NOT NULL | Average customer satisfaction over career |
| total_rates | INT | NOT NULL | Total number of ratings received |
| avg_rating | DECIMAL | NOT NULL | Average rating over career |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track career-wide performance metrics for sales agents.

---

#### 26. Individual Customer Potential Month (`individual_customer_potential_month`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| month | INT | NOT NULL | Month (1-12) |
| year | INT | NOT NULL | Year |
| lead_score | INT | NOT NULL | Lead score for the month |
| customer_tier | ENUM | NOT NULL | Tier: BRONZE, SILVER, GOLD, PLATINUM (see Constants.CustomerTierEnum) |
| lead_position | INT | NOT NULL | Lead ranking position |
| month_viewings_requested | INT | NOT NULL | Viewings requested in this month |
| month_viewings_attended | INT | NOT NULL | Viewings attended in this month |
| month_spending | DECIMAL | NOT NULL | Total spending in this month |
| month_purchases | INT | NOT NULL | Number of purchases in this month |
| month_rentals | INT | NOT NULL | Number of rentals in this month |
| month_contracts_signed | INT | NOT NULL | Contracts signed in this month |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track monthly customer engagement and potential for conversions.

---

#### 27. Individual Customer Potential All (`individual_customer_potential_all`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| customer_id | UUID | NOT NULL | Reference to customer |
| lead_score | INT | NOT NULL | Total lead score |
| lead_position | STRING | NOT NULL | Overall lead ranking position |
| viewings_requested | INT | NOT NULL | Total viewings requested |
| viewings_attended | INT | NOT NULL | Total viewings attended |
| spending | DECIMAL | NOT NULL | Total spending |
| total_purchases | INT | NOT NULL | Total number of purchases |
| total_rentals | INT | NOT NULL | Total number of rentals |
| total_contracts_signed | INT | NOT NULL | Total contracts signed |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track all-time customer engagement and potential.

---

#### 28. Individual Property Owner Contribution Month (`individual_property_owner_contribution_month`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| owner_id | UUID | NOT NULL | Reference to property owner |
| month | INT | NOT NULL | Month (1-12) |
| year | INT | NOT NULL | Year |
| contribution_point | INT | NOT NULL | Contribution points earned |
| contribution_tier | ENUM | NOT NULL | Tier: BRONZE, SILVER, GOLD, PLATINUM (see Constants.ContributionTierEnum) |
| ranking_position | INT | NOT NULL | Ranking position among owners |
| month_contribution_value | DECIMAL(15,2) | NOT NULL | Total contribution value for the month |
| month_total_properties | INT | NOT NULL | Total properties listed in this month |
| month_total_for_sales | INT | NOT NULL | Properties listed for sale in this month |
| month_total_for_rents | INT | NOT NULL | Properties listed for rent in this month |
| month_total_properties_sold | INT | NOT NULL | Properties sold in this month |
| month_total_properties_rented | INT | NOT NULL | Properties rented in this month |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track monthly contribution metrics for property owners.

---

#### 29. Individual Property Owner Contribution All (`individual_property_owner_contribution_all`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| owner_id | UUID | NOT NULL | Reference to property owner |
| contribution_point | INT | NOT NULL | Total contribution points |
| ranking_position | INT | NOT NULL | Overall ranking position |
| contribution_value | DECIMAL(15,2) | NOT NULL | Total contribution value |
| total_properties | INT | NOT NULL | Total properties listed |
| total_properties_sold | INT | NOT NULL | Total properties sold |
| total_properties_rented | INT | NOT NULL | Total properties rented |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track all-time contribution metrics for property owners.

---

### Reports

#### 30. Financial Report (`financial_reports`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| base_report_data | BaseReportData | NOT NULL | Base report metadata (see BaseReportData schema) |
| total_revenue_current_month | DECIMAL | NOT NULL | Total revenue for current month |
| total_revenue | DECIMAL | NOT NULL | Total revenue all-time |
| total_service_fees_current_month | DECIMAL | NOT NULL | Total service fees for current month |
| contract_count_current_month | DECIMAL | NOT NULL | Number of contracts in current month |
| contract_count | DECIMAL | NOT NULL | Total number of contracts all-time |
| tax | DECIMAL | NOT NULL | Tax amount |
| net_profit | DECIMAL | NOT NULL | Net profit |
| total_rates | DECIMAL | NOT NULL | Total number of ratings all-time |
| avg_rating | DECIMAL | NOT NULL | Average rating all-time |
| total_rates_current_month | DECIMAL | NOT NULL | Total ratings in current month |
| avg_rating_current_month | DECIMAL | NOT NULL | Average rating for current month |
| revenue_cities | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by city (all-time) |
| revenue_cities_current_month | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by city (current month) |
| revenue_districts | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by district (all-time) |
| revenue_districts_current_month | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by district (current month) |
| revenue_wards | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by ward (all-time) |
| revenue_wards_current_month | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by ward (current month) |
| revenue_property_types | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by property type (all-time) |
| revenue_property_types_current_month | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by property type (current month) |
| revenue_sales_agents | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by sales agent (all-time) |
| revenue_sales_agents_current_month | MAP<UUID, DECIMAL> | NOT NULL | Revenue breakdown by sales agent (current month) |
| sale_agents_salary_month | MAP<UUID, DECIMAL> | NOT NULL | Monthly salary for each sales agent |
| sale_agents_salary_career | MAP<UUID, DECIMAL> | NOT NULL | Career total salary for each sales agent |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Comprehensive financial reporting with revenue breakdowns.

---

#### 31. Customer Analytics Report (`customer_analytics_reports`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| base_report_data | BaseReportData | NOT NULL | Base report metadata (see BaseReportData schema) |
| total_customers | INT | NOT NULL | Total number of customers |
| new_customers_acquired_current_month | INT | NOT NULL | New customers acquired in current month |
| avg_customer_transaction_value | DECIMAL | NOT NULL | Average transaction value per customer |
| high_value_customer_count | INT | NOT NULL | Number of high-value customers |
| customer_satisfaction_score | DECIMAL | NOT NULL | Overall customer satisfaction score |
| total_rates | INT | NOT NULL | Total number of ratings all-time |
| avg_rating | DECIMAL | NOT NULL | Average rating all-time |
| total_rates_current_month | INT | NOT NULL | Total ratings in current month |
| avg_rating_current_month | DECIMAL | NOT NULL | Average rating for current month |
| list_potential_month | LIST<IndividualCustomerPotentialMonth> | NOT NULL | List of monthly customer potential records |
| list_potential_all | LIST<IndividualCustomerPotentialAll> | NOT NULL | List of all-time customer potential records |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Customer analytics and potential customer insights.

---

#### 32. Property Statistics Report (`property_statistic_reports`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| base_report_data | BaseReportData | NOT NULL | Base report metadata (see BaseReportData schema) |
| total_active_properties | INT | NOT NULL | Total number of active properties |
| total_sold_properties_current_month | INT | NOT NULL | Properties sold in current month |
| total_sold_properties | INT | NOT NULL | Total properties sold (current day) |
| total_rented_properties_current_month | INT | NOT NULL | Properties rented in current month |
| total_rented_properties | INT | NOT NULL | Total properties rented (current day) |
| searched_cities_month | MAP<UUID, INT> | NOT NULL | Search count by city (current month) |
| searched_cities | MAP<UUID, INT> | NOT NULL | Search count by city (all-time) |
| favorite_cities | MAP<UUID, INT> | NOT NULL | Favorite count by city |
| searched_districts_month | MAP<UUID, INT> | NOT NULL | Search count by district (current month) |
| searched_districts | MAP<UUID, INT> | NOT NULL | Search count by district (all-time) |
| favorite_districts | MAP<UUID, INT> | NOT NULL | Favorite count by district |
| searched_wards_month | MAP<UUID, INT> | NOT NULL | Search count by ward (current month) |
| searched_wards | MAP<UUID, INT> | NOT NULL | Search count by ward (all-time) |
| favorite_wards | MAP<UUID, INT> | NOT NULL | Favorite count by ward |
| searched_property_types_month | MAP<UUID, INT> | NOT NULL | Search count by property type (current month) |
| searched_property_types | MAP<UUID, INT> | NOT NULL | Search count by property type (all-time) |
| favorite_property_types | MAP<UUID, INT> | NOT NULL | Favorite count by property type |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Property statistics and search/favorite trends analysis.

---

#### 33. Agent Performance Report (`agent_performance_reports`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| base_report_data | BaseReportData | NOT NULL | Base report metadata (see BaseReportData schema) |
| total_agents | INT | NOT NULL | Total number of sales agents |
| avg_revenue_per_agent | DECIMAL | NOT NULL | Average revenue per agent |
| avg_customer_satisfaction | DECIMAL | NOT NULL | Average customer satisfaction across agents |
| total_rates | INT | NOT NULL | Total number of ratings all-time |
| avg_rating | DECIMAL | NOT NULL | Average rating all-time |
| total_rates_current_month | INT | NOT NULL | Total ratings in current month |
| avg_rating_current_month | DECIMAL | NOT NULL | Average rating for current month |
| list_performance_month | LIST<IndividualSalesAgentPerformanceMonth> | NOT NULL | List of monthly agent performance records |
| list_performance_career | LIST<IndividualSalesAgentPerformanceCareer> | NOT NULL | List of career agent performance records |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Sales agent performance tracking and reporting.

---

#### 34. Property Owner Contribution Report (`property_owner_contribution_report`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| base_report_data | BaseReportData | NOT NULL | Base report metadata (see BaseReportData schema) |
| total_owners | INT | NOT NULL | Total number of property owners |
| contribution_value | DECIMAL(15,2) | NOT NULL | Total contribution value |
| avg_owners_contribution_value | DECIMAL(15,2) | NOT NULL | Average contribution value per owner |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Property owner contribution tracking and reporting.

---

#### BaseReportData (Embedded Schema)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| report_type | ENUM | NOT NULL | Type: FINANCIAL, AGENT_PERFORMANCE, PROPERTY_STATISTICS, CUSTOMER_ANALYTICS, VIOLATION (see Constants.ReportTypeEnum) |
| month | INT | NULL | Report month (1-12) |
| year | INT | NULL | Report year |
| title | STRING | NULL | Report title |
| description | STRING | NULL | Report description |
| start_date | TIMESTAMP | NULL | Report period start date |
| end_date | TIMESTAMP | NULL | Report period end date |
| file_path | STRING | NULL | Path to generated report file |
| file_name | STRING | NULL | Generated report file name |
| file_format | STRING | NULL | Report file format (PDF, XLSX, etc.) |

**Purpose:** Common metadata for all report types.

---

### Search Logs

#### 35. Search Log (`search_logs`)

| Attribute | Data Type | Constraints | Description |
|-----------|-----------|-------------|-------------|
| id | STRING | PK, NOT NULL, AUTO | Unique identifier (UUID string) |
| user_id | UUID | NULL | Reference to user who searched |
| city_id | UUID | NULL | City searched |
| district_id | UUID | NULL | District searched |
| ward_id | UUID | NULL | Ward searched |
| property_id | UUID | NULL | Property viewed |
| property_type_id | UUID | NULL | Property type searched |
| created_at | TIMESTAMP | NOT NULL, AUTO | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, AUTO | Record last update timestamp |

**Purpose:** Track user search behavior for analytics and recommendations.

---

## Enum Reference (Constants.java)

All ENUM types used in the database are defined in `src/main/java/com/se100/bds/utils/Constants.java`:

- **RoleEnum**: ADMIN, SALESAGENT, GUEST, PROPERTY_OWNER, CUSTOMER
- **StatusProfileEnum**: ACTIVE, SUSPENDED, PENDING_APPROVAL, REJECTED
- **CustomerTierEnum**: BRONZE, SILVER, GOLD, PLATINUM
- **PerformanceTierEnum**: BRONZE, SILVER, GOLD, PLATINUM
- **ContributionTierEnum**: BRONZE, SILVER, GOLD, PLATINUM
- **AppointmentStatusEnum**: PENDING, CONFIRMED, COMPLETED, CANCELLED
- **ContractTypeEnum**: PURCHASE, RENTAL, INVESTMENT
- **ContractStatusEnum**: DRAFT, PENDING_SIGNING, ACTIVE, COMPLETED, CANCELLED
- **ContractPaymentTypeEnum**: MORTGAGE, MONTHLY_RENT, PAID_IN_FULL
- **PaymentTypeEnum**: DEPOSIT, ADVANCE, INSTALLMENT, FULL_PAY, MONTHLY, PENALTY, REFUND, MONEY_SALE, MONEY_RENTAL, SALARY
- **VerificationStatusEnum**: PENDING, VERIFIED, REJECTED
- **NotificationTypeEnum**: APPOINTMENT_REMINDER, CONTRACT_UPDATE, PAYMENT_DUE, VIOLATION_WARNING, SYSTEM_ALERT
- **RelatedEntityTypeEnum**: PROPERTY, CONTRACT, PAYMENT, APPOINTMENT, USER
- **NotificationStatusEnum**: PENDING, SENT, READ, FAILED
- **MediaTypeEnum**: IMAGE, VIDEO, DOCUMENT
- **TransactionTypeEnum**: SALE, RENTAL, INVESTMENT
- **OrientationEnum**: NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, UNKNOWN
- **PropertyStatusEnum**: PENDING, REJECTED, APPROVED, PAID, SOLD, RENTED, AVAILABLE, UNAVAILABLE, DELETED
- **ReportTypeEnum**: FINANCIAL, AGENT_PERFORMANCE, PROPERTY_STATISTICS, CUSTOMER_ANALYTICS, VIOLATION

---

## Database Design Notes

### PostgreSQL (Relational Database)
- Used for transactional data requiring ACID properties
- Handles user management, properties, contracts, payments, appointments
- Ensures data integrity through foreign key constraints
- Optimized for complex queries and joins

### MongoDB (Document Database)
- Used for analytics, logs, and dynamic data
- Stores customer preferences, search logs, rankings, and reports
- Provides flexibility for evolving data structures
- Optimized for aggregation and analytics queries
- Enables fast read/write for tracking user behavior

### Key Design Patterns
1. **Inheritance Mapping**: User types (Customer, SaleAgent, PropertyOwner) use shared primary key pattern
2. **Audit Fields**: All entities include created_at and updated_at timestamps
3. **Soft Delete**: Status fields allow for soft deletion instead of hard deletes
4. **Denormalization**: MongoDB schemas denormalize data for performance
5. **Aggregation**: Reports aggregate data from multiple sources for analytics

---

**Generated:** 2025-01-16  
**Version:** 1.0  
**Last Updated:** 2025-01-16

