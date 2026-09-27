import { createClient } from "./utils/supabase/server";
import { cookies } from "next/headers";

export default async function Page() {
  const cookieStore = await cookies();
  const supabase = createClient(cookieStore);

  const { data: bookings } = await supabase.from("bookings").select();

  return (
    <div style={{ padding: "2rem", fontFamily: "sans-serif" }}>
      <h1>WonderLight Cloud Admin — Supabase Connect</h1>
      <ul>
        {bookings?.map((b: any) => (
          <li key={b.id || b.booking_id}>
            {b.booking_id} — {b.title || b.service_category} ({b.status})
          </li>
        ))}
      </ul>
    </div>
  );
}
