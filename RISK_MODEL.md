# Risk Model

Mono OS Lite assigns every command a risk level before orchestration.

## Levels

| Level | Meaning | Demo action |
| --- | --- | --- |
| L0 | Passive / safe | Execute |
| L1 | Personal / low risk | Execute with audit logging |
| L2 | Sensitive / approval required | Hold until explicit approval |
| L3 | Financial / authentication required | Hold behind authentication gate |
| L4 | Critical / blocked in demo | Block execution |

## Trigger examples

- L0: general planning request.
- L1: calendar, priorities, startup roadmap, market research.
- L2: client email, private content, medical/passport/SSN terms.
- L3: transfer, bank, payment, wire, savings account, money movement.
- L4: delete account, wipe, terminate, legal filing, critical irreversible action.

## Required proof

The preset `Move $500 to my savings account` must classify as `Financial action`, assign `L3`, require authentication, and avoid real money movement. After demo authentication, Mono OS Lite executes only a mock transfer shell and logs the event.
