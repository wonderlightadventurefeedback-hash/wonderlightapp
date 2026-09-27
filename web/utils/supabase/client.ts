import { createBrowserClient } from "@supabase/ssr";

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || "https://elcahnyfaogvittfrxaa.supabase.co";
const supabaseKey = process.env.NEXT_PUBLIC_SUPABASE_PUBLISHABLE_KEY || "sb_publishable_Yj7EQj1ZKy3ro5cEGt0r7A_EhUttYan";

export const createClient = () =>
  createBrowserClient(
    supabaseUrl,
    supabaseKey,
  );
