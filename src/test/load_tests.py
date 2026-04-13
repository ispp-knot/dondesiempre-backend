from locust import FastHttpUser, task, between
import random

PASSWORD = "Password123!"
CLIENT_TO_STORE_RATIO = 10 # Amount of clients per store
TASK_TO_LOGIN_RATIO = 10 # Amount of logins per other tasks


class BaseUser(FastHttpUser):
    wait_time = between(1, 5)
    abstract = True

    def login(self, email):
        response = self.client.post(
            "/api/v1/auth/login",
            json={"email": email, "password": PASSWORD},
        )

        if response.status_code != 200:
            response.failure(f"Login failed for {email}")
            return None

        data = response.json()
        return data


# =========================
# CLIENT USER
# =========================
class ClientUser(BaseUser):
    weight = CLIENT_TO_STORE_RATIO

    def on_start(self):
        self.email = f"client{random.randint(1, 10)}@client.com"
        data = self.login(self.email)

        self.token = data["token"] if data else None
        self.headers = {"Authorization": f"Bearer {self.token}"} if self.token else {}

    @task(TASK_TO_LOGIN_RATIO)
    def client_flow(self):
        if not self.token:
            return

        # 1. Get all stores
        stores_resp = self.client.get(
            "/api/v1/stores",
            headers=self.headers,
        )

        if stores_resp.status_code != 200:
            return

        stores = stores_resp.json()
        if not stores:
            return

        # Pick random store
        store = random.choice(stores)
        store_id = store["id"]
        store_name = store["name"]

        # 2. Search by name
        search_resp = self.client.get(
            f"/api/v1/stores?name={store_name.split(" ")[0]}",
            headers=self.headers,
            name="/api/v1/stores?name"
        )

        if search_resp.status_code != 200:
            return

        # 3. Get store details
        self.client.get(
            f"/api/v1/stores/{store_id}",
            headers=self.headers,
            name="/api/v1/stores/id"
        )

        # 4. Get products
        products_resp = self.client.get(
            f"/api/v1/stores/{store_id}/products",
            headers=self.headers,
            name="/api/v1/stores/id/products"
        )

        if products_resp.status_code != 200:
            return

        products = products_resp.json()
        if not products:
            return

        # 5. Place order
        order_body = {
            product["id"]: 2
            for product in products
        }

        self.client.post(
            "/api/v1/orders",
            json=order_body,
            headers=self.headers,
        )

    @task(1)
    def login_task(self):
        self.on_start()


# =========================
# STORE USER
# =========================
class StoreUser(BaseUser):
    weight = 1

    STORE_EMAILS = [
        "demo@gretacloset.com",
        "demo@margovantes.com",
        "demo@pineapplemoda.com",
        "demo@roire.com",
        "demo@romantikavintage.com",
        "demo@confeccionesyhogarsansebastian.com",
    ]

    def on_start(self):
        self.email = random.choice(self.STORE_EMAILS)
        data = self.login(self.email)

        if not data:
            self.token = None
            self.headers = {}
            self.store_id = None
            return

        self.token = data["token"]
        self.headers = {"Authorization": f"Bearer {self.token}"}

        # Extract store ID from login response
        try:
            self.store_id = data["user"]["store"]["id"]
        except Exception:
            self.store_id = None

    @task(TASK_TO_LOGIN_RATIO)
    def store_flow(self):
        if not self.token or not self.store_id:
            return

        # 1. Get own store
        self.client.get(
            f"/api/v1/stores/{self.store_id}",
            headers=self.headers,
            name="/api/v1/stores/id"
        )

        # 2. Get orders
        orders_resp = self.client.get(
            "/api/v1/orders",
            headers=self.headers,
        )

        if orders_resp.status_code != 200:
            return

        orders = orders_resp.json()

        # 3. Confirm pending orders
        for order in orders:
            if order.get("orderStatus") != "PENDING":
                continue

            self.client.patch(
                f"/api/v1/orders/{order['id']}/confirm",
                headers=self.headers,
                name="/api/v1/orders/id/confirm"
            )

    @task(1)
    def login_task(self):
        self.on_start()