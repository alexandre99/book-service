curl -i -X POST http://localhost:8080/property \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Studio RM - Seafront!",
           "ownerId": "6a598ba2-07c3-4036-ac96-683075706b8d",
           "amenities":["Elevator", "Netflix"]
         }'
