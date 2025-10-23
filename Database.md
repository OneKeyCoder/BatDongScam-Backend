# Database Schema Documentation

## Overview
This document describes the database schema for the Real Estate System (RES-Backend). The system uses:
- **PostgreSQL** for relational data (entities)
- **MongoDB** for analytics, reports, and customer preferences

---

## PostgreSQL Tables (Relational Database)

### 1. Users and Authentication

#### Table: `users`
Base user table with authentication information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | UUID | PK, NOT NULL | Primary key |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password | VARCHAR(255) | NOT NULL | Encrypted password |
| email | VARCHAR(100) | UNIQUE, NOT NULL | User email |
| phone_number | VARCHAR(20) | NOT NULL | Contact phone |
| full_name | VARCHAR(100) | NOT NULL | Full name |
| avatar_url | TEXT | NOT NULL | Profile picture URL |
| role | ENUM | NOT NULL | USER, CUSTOMER, PROPERTY_OWNER, SALE_AGENT, ADMIN |
| status | ENUM | NOT NULL | ACTIVE, INACTIVE, BANNED |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `customers`
Customer-specific information (inherits from users).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| customer_id | UUID | PK, FK(users) | References user_id |
| tier | ENUM | NOT NULL | BRONZE, SILVER, GOLD, PLATINUM |

#### Table: `sale_agents`
Sale agent-specific information (inherits from users).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| agent_id | UUID | PK, FK(users) | References user_id |
| performance_tier | ENUM | NOT NULL | BRONZE, SILVER, GOLD, PLATINUM, DIAMOND |
| commission_rate | DECIMAL(5,4) | NOT NULL | Commission percentage |

#### Table: `property_owners`
Property owner-specific information (inherits from users).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| owner_id | UUID | PK, FK(users) | References user_id |
| contribution_tier | ENUM | NOT NULL | BRONZE, SILVER, GOLD, PLATINUM, DIAMOND |

---

### 2. Location Management

#### Table: `cities`
City/Province information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| city_id | UUID | PK, NOT NULL | Primary key |
| name | VARCHAR(100) | NOT NULL | City name |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `districts`
District information within cities.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| district_id | UUID | PK, NOT NULL | Primary key |
| city_id | UUID | FK(cities), NOT NULL | Parent city |
| name | VARCHAR(100) | NOT NULL | District name |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `wards`
Ward/Commune information within districts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| ward_id | UUID | PK, NOT NULL | Primary key |
| district_id | UUID | FK(districts), NOT NULL | Parent district |
| name | VARCHAR(100) | NOT NULL | Ward name |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 3. Property Management

#### Table: `property_types`
Types of properties (House, Apartment, Land, etc.).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| property_type_id | UUID | PK, NOT NULL | Primary key |
| name | VARCHAR(100) | NOT NULL | Type name |
| description | TEXT | NOT NULL | Type description |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `properties`
Main property listing information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| property_id | UUID | PK, NOT NULL | Primary key |
| owner_id | UUID | FK(property_owners), NOT NULL | Property owner |
| property_type_id | UUID | FK(property_types), NOT NULL | Type of property |
| ward_id | UUID | FK(wards), NOT NULL | Location ward |
| title | VARCHAR(200) | NOT NULL | Property title |
| description | TEXT | NOT NULL | Full description |
| address | VARCHAR(255) | NOT NULL | Street address |
| latitude | DECIMAL(10,8) | NOT NULL | GPS latitude |
| longitude | DECIMAL(11,8) | NOT NULL | GPS longitude |
| min_area | DECIMAL(10,2) | NOT NULL | Minimum area in m² |
| max_area | DECIMAL(10,2) | NOT NULL | Maximum area in m² |
| num_bedrooms | INTEGER | NOT NULL | Number of bedrooms |
| num_bathrooms | INTEGER | NOT NULL | Number of bathrooms |
| num_floors | INTEGER | NOT NULL | Number of floors |
| orientation | ENUM | NOT NULL | NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST |
| transaction_type | ENUM | NOT NULL | SALE, RENT, BOTH |
| sale_price | DECIMAL(15,2) | NOT NULL | Sale price |
| rent_price | DECIMAL(15,2) | NOT NULL | Monthly rent price |
| status | ENUM | NOT NULL | AVAILABLE, PENDING, SOLD, RENTED, UNAVAILABLE |
| verified | BOOLEAN | NOT NULL | Verification status |
| view_count | INTEGER | NOT NULL, DEFAULT 0 | Number of views |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `media`
Media files (images, videos) for properties.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| media_id | UUID | PK, NOT NULL | Primary key |
| property_id | UUID | FK(properties), NOT NULL | Associated property |
| url | TEXT | NOT NULL | Media file URL |
| media_type | ENUM | NOT NULL | IMAGE, VIDEO |
| display_order | INTEGER | NOT NULL | Display order |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 4. Appointments

#### Table: `appointment`
Property viewing appointments.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| appointment_id | UUID | PK, NOT NULL | Primary key |
| property_id | UUID | FK(properties), NOT NULL | Property to view |
| customer_id | UUID | FK(customers), NOT NULL | Customer |
| agent_id | UUID | FK(sale_agents), NOT NULL | Assigned agent |
| appointment_date | TIMESTAMP | NOT NULL | Scheduled date/time |
| status | ENUM | NOT NULL | PENDING, CONFIRMED, COMPLETED, CANCELLED |
| notes | TEXT | NOT NULL | Additional notes |
| cancellation_reason | TEXT | NOT NULL | Reason if cancelled |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 5. Contracts and Payments

#### Table: `contract`
Property transaction contracts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| contract_id | UUID | PK, NOT NULL | Primary key |
| property_id | UUID | FK(properties), NOT NULL | Associated property |
| customer_id | UUID | FK(customers), NOT NULL | Buyer/Renter |
| agent_id | UUID | FK(sale_agents), NOT NULL | Handling agent |
| contract_type | ENUM | NOT NULL | SALE, RENT |
| contract_number | VARCHAR(50) | UNIQUE, NOT NULL | Contract number |
| start_date | DATE | NOT NULL | Contract start date |
| end_date | DATE | NOT NULL | Contract end date |
| special_terms | TEXT | NOT NULL | Special terms |
| status | ENUM | NOT NULL | DRAFT, ACTIVE, COMPLETED, CANCELLED |
| cancellation_reason | TEXT | NOT NULL | Reason if cancelled |
| cancellation_penalty | DECIMAL(15,2) | NOT NULL | Cancellation penalty |
| cancelled_by | ENUM | NOT NULL | Role who cancelled |
| contract_payment_type | ENUM | NOT NULL | FULL_PAYMENT, INSTALLMENT, PROGRESS_BASED |
| total_contract_amount | DECIMAL(15,2) | NOT NULL | Total amount |
| deposit_amount | DECIMAL(15,2) | NOT NULL | Deposit paid |
| remaining_amount | DECIMAL(15,2) | NOT NULL | Amount remaining |
| advance_payment_amount | DECIMAL(15,2) | NOT NULL | Advance payment |
| installment_amount | INTEGER | NOT NULL | Number of installments |
| progress_milestone | DECIMAL(15,2) | NOT NULL | Progress milestone % |
| final_payment_amount | DECIMAL(15,2) | NOT NULL | Final payment amount |
| late_payment_penalty_rate | DECIMAL(5,4) | NOT NULL | Late penalty rate |
| special_conditions | TEXT | NOT NULL | Special conditions |
| signed_at | TIMESTAMP | NOT NULL | Signing timestamp |
| completed_at | TIMESTAMP | NOT NULL | Completion timestamp |
| rating | SMALLINT | | Customer rating (1-5) |
| comment | TEXT | | Customer comment |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `payments`
Payment records for contracts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| payment_id | UUID | PK, NOT NULL | Primary key |
| contract_id | UUID | FK(contract), NOT NULL | Associated contract |
| payment_type | ENUM | NOT NULL | DEPOSIT, INSTALLMENT, FINAL, FULL |
| amount | DECIMAL(15,2) | NOT NULL | Payment amount |
| payment_date | TIMESTAMP | NOT NULL | Payment date |
| due_date | DATE | NOT NULL | Due date |
| payment_method | VARCHAR(50) | NOT NULL | Payment method |
| transaction_id | VARCHAR(100) | NOT NULL | Transaction reference |
| notes | TEXT | NOT NULL | Payment notes |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 6. Documents

#### Table: `document_types`
Types of identification documents.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| document_type_id | UUID | PK, NOT NULL | Primary key |
| name | VARCHAR(100) | NOT NULL | Document type name |
| description | TEXT | NOT NULL | Description |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

#### Table: `identification_documents`
User identification documents.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| document_id | UUID | PK, NOT NULL | Primary key |
| user_id | UUID | FK(users), NOT NULL | Document owner |
| document_type_id | UUID | FK(document_types), NOT NULL | Type of document |
| document_number | VARCHAR(50) | NOT NULL | Document number |
| issue_date | DATE | NOT NULL | Issue date |
| expiry_date | DATE | NOT NULL | Expiry date |
| issuing_authority | VARCHAR(100) | NOT NULL | Issuing authority |
| front_image_url | TEXT | NOT NULL | Front image URL |
| back_image_url | TEXT | NOT NULL | Back image URL |
| verification_status | ENUM | NOT NULL | PENDING, VERIFIED, REJECTED |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 7. Notifications

#### Table: `notifications`
User notifications.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| notification_id | UUID | PK, NOT NULL | Primary key |
| user_id | UUID | FK(users), NOT NULL | Recipient user |
| type | ENUM | NOT NULL | APPOINTMENT, CONTRACT, PAYMENT, SYSTEM, etc. |
| title | VARCHAR(200) | NOT NULL | Notification title |
| message | TEXT | NOT NULL | Notification message |
| related_entity_type | ENUM | NOT NULL | PROPERTY, CONTRACT, APPOINTMENT, etc. |
| related_entity_id | UUID | NOT NULL | Related entity ID |
| status | ENUM | NOT NULL | UNREAD, READ, ARCHIVED |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

### 8. Violation Reports

#### Table: `violation_reports`
Reports of policy violations.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| report_id | UUID | PK, NOT NULL | Primary key |
| reporter_id | UUID | FK(users), NOT NULL | User who reported |
| reported_entity_type | ENUM | NOT NULL | PROPERTY, USER, etc. |
| reported_entity_id | UUID | NOT NULL | Reported entity ID |
| violation_type | VARCHAR(100) | NOT NULL | Type of violation |
| description | TEXT | NOT NULL | Violation description |
| status | ENUM | NOT NULL | PENDING, REVIEWED, RESOLVED |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| deleted_at | TIMESTAMP | | Soft delete timestamp |

---

## MongoDB Collections (NoSQL Database)

### 1. Search Logs

#### Collection: `search_logs`
Tracks user search behavior for analytics.

```javascript
{
  _id: ObjectId,
  user_id: UUID,
  city_id: UUID,
  district_id: UUID,
  ward_id: UUID,
  property_id: UUID,
  property_type_id: UUID,
  created_at: DateTime,
  updated_at: DateTime
}
```

---

### 2. Customer Preferences

#### Collection: `customer_favorite_properties`
Customer's favorite/saved properties.

```javascript
{
  _id: ObjectId,
  customer_id: UUID,
  ref_id: UUID,  // property_id
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `customer_preferred_cities`
Customer's preferred cities based on behavior.

```javascript
{
  _id: ObjectId,
  customer_id: UUID,
  ref_id: UUID,  // city_id
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `customer_preferred_districts`
Customer's preferred districts.

```javascript
{
  _id: ObjectId,
  customer_id: UUID,
  ref_id: UUID,  // district_id
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `customer_preferred_wards`
Customer's preferred wards.

```javascript
{
  _id: ObjectId,
  customer_id: UUID,
  ref_id: UUID,  // ward_id
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `customer_preferred_property_types`
Customer's preferred property types.

```javascript
{
  _id: ObjectId,
  customer_id: UUID,
  ref_id: UUID,  // property_type_id
  created_at: DateTime,
  updated_at: DateTime
}
```

---

### 3. Monthly Reports

All reports are generated once per month and stored for historical analysis.

#### Collection: `property_statistic_reports`
Property and search statistics report.

```javascript
{
  _id: ObjectId,
  report_month: Integer,     // 1-12
  report_year: Integer,      // e.g., 2025
  
  // Property counts
  total_active_properties: Integer,
  total_sold_properties_current_month: Integer,
  total_sold_properties: Integer,      // all-time daily count
  total_rented_properties_current_month: Integer,
  total_rented_properties: Integer,    // all-time daily count
  
  // Most searched cities (with pagination support)
  searched_cities_month: [
    { id: UUID, count: Integer }  // RankedItem - current month
  ],
  searched_cities: [
    { id: UUID, count: Integer }  // RankedItem - all-time
  ],
  
  // Most favorited cities
  favorite_cities: [
    { id: UUID, count: Integer }  // RankedItem
  ],
  
  // Most searched districts
  searched_districts_month: [
    { id: UUID, count: Integer }
  ],
  searched_districts: [
    { id: UUID, count: Integer }
  ],
  
  // Most favorited districts
  favorite_districts: [
    { id: UUID, count: Integer }
  ],
  
  // Most searched wards
  searched_wards_month: [
    { id: UUID, count: Integer }
  ],
  searched_wards: [
    { id: UUID, count: Integer }
  ],
  
  // Most favorited wards
  favorite_wards: [
    { id: UUID, count: Integer }
  ],
  
  // Most searched property types
  searched_property_types_month: [
    { id: UUID, count: Integer }
  ],
  searched_property_types: [
    { id: UUID, count: Integer }
  ],
  
  // Most favorited property types
  favorite_property_types: [
    { id: UUID, count: Integer }
  ],
  
  // Most searched individual properties
  searched_properties_month: [
    { id: UUID, count: Integer }
  ],
  searched_properties: [
    { id: UUID, count: Integer }
  ],
  
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `financial_reports`
Financial performance and revenue report.

```javascript
{
  _id: ObjectId,
  report_month: Integer,
  report_year: Integer,
  
  // Revenue metrics
  total_revenue_current_month: Decimal,
  total_revenue: Decimal,                    // all-time
  total_service_fees_current_month: Decimal,
  
  // Contract metrics
  contract_count_current_month: Decimal,
  contract_count: Decimal,                   // all-time
  
  // Financial calculations
  tax: Decimal,
  net_profit: Decimal,
  
  // Rating metrics
  total_rates: Decimal,                      // all-time count
  avg_rating: Decimal,                       // all-time average
  total_rates_current_month: Decimal,
  avg_rating_current_month: Decimal,
  
  // Revenue by location (with pagination support)
  revenue_cities: [
    { id: UUID, revenue: Decimal }           // RankedRevenueItem - all-time
  ],
  revenue_cities_current_month: [
    { id: UUID, revenue: Decimal }           // current month
  ],
  
  revenue_districts: [
    { id: UUID, revenue: Decimal }
  ],
  revenue_districts_current_month: [
    { id: UUID, revenue: Decimal }
  ],
  
  revenue_wards: [
    { id: UUID, revenue: Decimal }
  ],
  revenue_wards_current_month: [
    { id: UUID, revenue: Decimal }
  ],
  
  // Revenue by property type
  revenue_property_types: [
    { id: UUID, revenue: Decimal }
  ],
  revenue_property_types_current_month: [
    { id: UUID, revenue: Decimal }
  ],
  
  // Revenue by sales agent
  revenue_sales_agents: [
    { id: UUID, revenue: Decimal }
  ],
  revenue_sales_agents_current_month: [
    { id: UUID, revenue: Decimal }
  ],
  
  // Sales agent salaries
  sale_agents_salary_month: [
    { id: UUID, revenue: Decimal }           // current month salary
  ],
  sale_agents_salary_career: [
    { id: UUID, revenue: Decimal }           // all-time salary
  ],
  
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `agent_performance_reports`
Sales agent performance metrics.

```javascript
{
  _id: ObjectId,
  report_month: Integer,
  report_year: Integer,
  
  // Agent metrics
  total_agents: Integer,
  avg_revenue_per_agent: Decimal,
  avg_customer_satisfaction: Decimal,
  
  // Rating metrics
  total_rates: Integer,                      // all-time
  avg_rating: Decimal,                       // all-time
  total_rates_current_month: Integer,
  avg_rating_current_month: Decimal,
  
  // Individual agent performance (with pagination support)
  list_performance_month: [
    {
      agent_id: UUID,
      total_contracts: Integer,
      total_revenue: Decimal,
      avg_rating: Decimal,
      performance_tier: String               // BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }
  ],
  
  list_performance_career: [
    {
      agent_id: UUID,
      total_contracts: Integer,
      total_revenue: Decimal,
      avg_rating: Decimal,
      performance_tier: String
    }
  ],
  
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `customer_analytics_reports`
Customer behavior and value analytics.

```javascript
{
  _id: ObjectId,
  report_month: Integer,
  report_year: Integer,
  
  // Customer metrics
  total_customers: Integer,
  new_customers_acquired_current_month: Integer,
  avg_customer_transaction_value: Decimal,
  high_value_customer_count: Integer,
  customer_satisfaction_score: Decimal,
  
  // Rating metrics
  total_rates: Integer,
  avg_rating: Decimal,
  total_rates_current_month: Integer,
  avg_rating_current_month: Decimal,
  
  // Individual customer potential (with pagination support)
  list_potential_month: [
    {
      customer_id: UUID,
      total_contracts: Integer,
      total_spent: Decimal,
      avg_rating: Decimal,
      customer_tier: String                  // BRONZE, SILVER, GOLD, PLATINUM
    }
  ],
  
  list_potential_all: [
    {
      customer_id: UUID,
      total_contracts: Integer,
      total_spent: Decimal,
      avg_rating: Decimal,
      customer_tier: String
    }
  ],
  
  created_at: DateTime,
  updated_at: DateTime
}
```

#### Collection: `property_owner_contribution_report`
Property owner contribution metrics.

```javascript
{
  _id: ObjectId,
  report_month: Integer,
  report_year: Integer,
  
  // Owner metrics
  total_owners: Integer,
  contribution_value: Decimal,
  avg_owners_contribution_value: Decimal,
  
  // Individual owner contribution (with pagination support)
  list_contribution_month: [
    {
      owner_id: UUID,
      total_properties: Integer,
      total_contracts: Integer,
      total_revenue: Decimal,
      contribution_tier: String              // BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }
  ],
  
  list_contribution_all: [
    {
      owner_id: UUID,
      total_properties: Integer,
      total_contracts: Integer,
      total_revenue: Decimal,
      contribution_tier: String
    }
  ],
  
  created_at: DateTime,
  updated_at: DateTime
}
```

---

## Enumerations (Constants)

### RoleEnum
- `USER` - Basic user
- `CUSTOMER` - Customer/Buyer
- `PROPERTY_OWNER` - Property owner
- `SALE_AGENT` - Sales agent
- `ADMIN` - System administrator

### StatusProfileEnum
- `ACTIVE` - Active account
- `INACTIVE` - Inactive account
- `BANNED` - Banned account

### CustomerTierEnum
- `BRONZE` - Bronze tier
- `SILVER` - Silver tier
- `GOLD` - Gold tier
- `PLATINUM` - Platinum tier

### PerformanceTierEnum
- `BRONZE` - Bronze performance
- `SILVER` - Silver performance
- `GOLD` - Gold performance
- `PLATINUM` - Platinum performance
- `DIAMOND` - Diamond performance

### ContributionTierEnum
- `BRONZE` - Bronze contribution
- `SILVER` - Silver contribution
- `GOLD` - Gold contribution
- `PLATINUM` - Platinum contribution
- `DIAMOND` - Diamond contribution

### AppointmentStatusEnum
- `PENDING` - Awaiting confirmation
- `CONFIRMED` - Confirmed appointment
- `COMPLETED` - Completed appointment
- `CANCELLED` - Cancelled appointment

### ContractTypeEnum
- `SALE` - Sale contract
- `RENT` - Rental contract

### ContractStatusEnum
- `DRAFT` - Draft contract
- `ACTIVE` - Active contract
- `COMPLETED` - Completed contract
- `CANCELLED` - Cancelled contract

### ContractPaymentTypeEnum
- `FULL_PAYMENT` - Full payment upfront
- `INSTALLMENT` - Payment in installments
- `PROGRESS_BASED` - Payment based on progress

### PaymentTypeEnum
- `DEPOSIT` - Deposit payment
- `INSTALLMENT` - Installment payment
- `FINAL` - Final payment
- `FULL` - Full payment

### VerificationStatusEnum
- `PENDING` - Pending verification
- `VERIFIED` - Verified
- `REJECTED` - Rejected

### NotificationTypeEnum
- `APPOINTMENT` - Appointment notification
- `CONTRACT` - Contract notification
- `PAYMENT` - Payment notification
- `SYSTEM` - System notification
- `PROPERTY_UPDATE` - Property update
- `MESSAGE` - Message notification

### NotificationStatusEnum
- `UNREAD` - Unread notification
- `READ` - Read notification
- `ARCHIVED` - Archived notification

### MediaTypeEnum
- `IMAGE` - Image file
- `VIDEO` - Video file

### TransactionTypeEnum
- `SALE` - For sale
- `RENT` - For rent
- `BOTH` - Both sale and rent

### OrientationEnum
- `NORTH` - North facing
- `SOUTH` - South facing
- `EAST` - East facing
- `WEST` - West facing
- `NORTHEAST` - Northeast facing
- `NORTHWEST` - Northwest facing
- `SOUTHEAST` - Southeast facing
- `SOUTHWEST` - Southwest facing

### PropertyStatusEnum
- `AVAILABLE` - Available for transaction
- `PENDING` - Pending transaction
- `SOLD` - Sold
- `RENTED` - Rented
- `UNAVAILABLE` - Unavailable

### RelatedEntityTypeEnum
- `PROPERTY` - Property entity
- `CONTRACT` - Contract entity
- `APPOINTMENT` - Appointment entity
- `PAYMENT` - Payment entity
- `USER` - User entity

---

## Key Design Decisions

### PostgreSQL Usage
- **Relational data**: Users, properties, contracts, payments
- **ACID compliance**: Critical for financial transactions
- **Foreign key constraints**: Ensure data integrity
- **Soft deletes**: All tables use `deleted_at` for soft deletion

### MongoDB Usage
- **Analytics and reports**: Generated monthly, stored for historical analysis
- **Search logs**: High-write volume tracking
- **Customer preferences**: Flexible schema for behavior tracking
- **Pagination support**: Lists use offset/limit for efficient queries

### Report Generation Strategy
- Reports are generated **once per month** (not in real-time)
- All ranked lists (cities, districts, agents, etc.) support pagination via **offset and limit**
- Data is pre-sorted and stored to avoid runtime sorting overhead
- Uses `RankedItem` for count-based rankings and `RankedRevenueItem` for revenue-based rankings

### UUID Primary Keys
- All entities use UUID for primary keys
- Better for distributed systems and security
- No sequential ID leakage

### Timestamps
- All tables/collections have `created_at` and `updated_at`
- Enables audit trails and data lineage

---

## Indexes (Recommended)

### PostgreSQL Indexes
```sql
-- Users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Properties
CREATE INDEX idx_properties_owner_id ON properties(owner_id);
CREATE INDEX idx_properties_ward_id ON properties(ward_id);
CREATE INDEX idx_properties_type_id ON properties(property_type_id);
CREATE INDEX idx_properties_status ON properties(status);
CREATE INDEX idx_properties_transaction_type ON properties(transaction_type);

-- Contracts
CREATE INDEX idx_contracts_property_id ON contract(property_id);
CREATE INDEX idx_contracts_customer_id ON contract(customer_id);
CREATE INDEX idx_contracts_agent_id ON contract(agent_id);
CREATE INDEX idx_contracts_status ON contract(status);
CREATE INDEX idx_contracts_signed_at ON contract(signed_at);

-- Payments
CREATE INDEX idx_payments_contract_id ON payments(contract_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
```

### MongoDB Indexes
```javascript
// Search logs
db.search_logs.createIndex({ user_id: 1, created_at: -1 });
db.search_logs.createIndex({ city_id: 1 });
db.search_logs.createIndex({ property_id: 1 });

// Customer preferences
db.customer_favorite_properties.createIndex({ customer_id: 1 });
db.customer_preferred_cities.createIndex({ customer_id: 1 });

// Reports
db.property_statistic_reports.createIndex({ report_year: -1, report_month: -1 });
db.financial_reports.createIndex({ report_year: -1, report_month: -1 });
db.agent_performance_reports.createIndex({ report_year: -1, report_month: -1 });
```

---

## Migration Notes

### Property Area Changes
- **Previous**: Single `total_area` field
- **Current**: Separate `min_area` and `max_area` fields
- **Reason**: Support properties with variable area ranges

### Report Pagination
- All report lists (searched cities, revenue by location, agent performance, etc.) support pagination
- Use `offset` and `limit` parameters when querying
- Data is pre-sorted in descending order (highest first)

---

*Last Updated: 2025-10-23*

